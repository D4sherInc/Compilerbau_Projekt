// visits parse tree
// generates a .j jasmine file

import analysis.DepthFirstAdapter;
import node.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class CodeGenerator extends DepthFirstAdapter {

    private final File jasmin;
    private final Start tree;
    private final SymbolTable symbolTable;
    private StringBuilder jasminString;


    private static final Type INTEGER = Type.INTEGER;
    private static final Type DOUBLE = Type.DOUBLE;
    private static final Type BOOLEAN = Type.BOOLEAN;
    private static final Type STRING = Type.STRING;
    private static final Type VOID = Type.VOID;

    // get all vars from symbolTable
    private final Map<String, Set<String>> method_vars;

    // size counter for stack for new vars
    private int stackCounter;
    // saves all vars on stack for future 'istore' and 'iload' calls
    // saved as:
    //  first_var   ->  1
    //  second_var  ->  2
    //  random_name ->  3
    private Map<String, Integer> varsOnStack;


    private String currentMethod;
    private Type currentType;
    private final Stack<Type> topStackPeek;
    private int branchCounter;

    public CodeGenerator(SymbolTable symbolTable, Start tree, String filepath) {
        this.jasmin = new File(filepath);
        this.tree = tree;
        this.method_vars = new HashMap<>();
        this.symbolTable = symbolTable;
        this.topStackPeek = new Stack<>();

        // add all vars in HashMap
        for (String method_name : symbolTable.getMethodInfos().keySet()) {
            method_vars.put(method_name, symbolTable.get_var_and_param_names(method_name));
        }

        // cut: '/path/to/filename.j' -> 'filename'
        String filename = filepath.substring(filepath.lastIndexOf("/") + 1, filepath.length() - 2);

        generateCode(filename);
    }

    public File getJasmin() {
        System.out.println("\n\nfinal stack: \n" + topStackPeek.toString());
        return jasmin;
    }

    private String generateCode(String filename) {
        stackCounter = 0;
        varsOnStack = new HashMap<>();

        //generate program specific jasmin code
        String jasminOutput = visitParseTree(tree);

        try {
            FileWriter fw = new FileWriter(jasmin);

            // base jasmin code prefix
            fw.write(".bytecode 49.0\n" +
                    ".source " + filename + ".j\n"+
                    ".class public " + filename + "\n" +
                    ".super java/lang/Object\n" +
                    "\n");

            fw.write(jasminOutput);
            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return jasminOutput;

    }

    // create jasmin code for input
    private String visitParseTree(Start tree) {

        jasminString = new StringBuilder();
        tree.apply(this);

        return jasminString.toString();
    }

    //TODO:
    // for every AST node, add code to jasmin file

    @Override
    public void caseStart(Start node) {

        super.caseStart(node);
    }

    @Override
    public void caseAProgramStartAbstract(AProgramStartAbstract node) {
        LinkedList<PMethodDeclarationAbstract> nonMainStuff1 = node.getNonMainStuff1();
        PMainMethodAbstract mainMethod = node.getMainMethod();
        LinkedList<PMethodDeclarationAbstract> nonMainStuff2 = node.getNonMainStuff2();

        for (PMethodDeclarationAbstract subNode : nonMainStuff1) subNode.apply(this);

        mainMethod.apply(this);

        for (PMethodDeclarationAbstract subNode : nonMainStuff2) subNode.apply(this);

    }

    @Override
    public void caseAMainMainMethodAbstract(AMainMainMethodAbstract node) {
        currentMethod = "Main";
        start_new_method(currentMethod);

        PStatementAbstract statementAbstract = node.getStatementAbstract();
        jasminString.append(".method public static main([Ljava/lang/String;)V\n" +
                "\t.limit stack 20\n" +
                "\t.limit locals ").append(varsOnStack.keySet().size() + 1).append("\n");
        statementAbstract.apply(this);

        jasminString.append("\treturn\n" +
                "\t.end method\n" +
                "\n");

        currentMethod = null;

    }

    @Override
    public void caseATypeMethodDeclarationAbstract(ATypeMethodDeclarationAbstract node) {
        TIdentifier identifier = node.getIdentifier();
        PStatementAbstract codeBlock = node.getCodeBlock();

        currentMethod = identifier.getText();
        start_new_method(currentMethod);


        jasminString.append(".method public static ").append(currentMethod).append("(");

        // append one for every type of parameter
        for (Type t : symbolTable.getMethodInfos().get(currentMethod).getParams()) {
            if (t == INTEGER) jasminString.append("I");
            else if(t == DOUBLE) jasminString.append("D");
            else if(t == BOOLEAN) jasminString.append("Z");
            else if(t == STRING) jasminString.append("Ljava/lang/String;");
        }
        jasminString.append(")");

        // append return type
        Type returnType = symbolTable.get_method_return_type(currentMethod);
        if (returnType == INTEGER) jasminString.append("I");
        else if(returnType == DOUBLE) jasminString.append("D");
        else if(returnType == BOOLEAN) jasminString.append("Z");
        else if(returnType == STRING) jasminString.append("Ljava/lang/String;");
        else if(returnType == VOID) jasminString.append("V");

        jasminString.append("\n\t.limit stack 20\n" +
                "\t.limit locals ").append(varsOnStack.keySet().size() + 1).append("\n");

        codeBlock.apply(this);

        jasminString.append("\t.end method\n" +
                "\n");

        topStackPeek.pop();
        currentMethod = null;
    }

    @Override
    public void caseAVoidMethodDeclarationAbstract(AVoidMethodDeclarationAbstract node) {
        TIdentifier identifier = node.getIdentifier();
        PStatementAbstract codeBlock = node.getCodeBlock();

        currentMethod = identifier.getText();
        start_new_method(currentMethod);


        jasminString.append(".method public static ").append(currentMethod).append("(");

        // append one for every type of parameter
        for (Type t : symbolTable.getMethodInfos().get(currentMethod).getParams()) {
            if (t == INTEGER) jasminString.append("I");
            else if(t == DOUBLE) jasminString.append("D");
            else if(t == BOOLEAN) jasminString.append("Z");
            else if(t == STRING) jasminString.append("Ljava/lang/String;");
        }
        // return type = void -> V
        jasminString.append(")V\n" +
                "\t.limit stack 20\n" +
                "\t.limit locals ").append(varsOnStack.keySet().size() + 1).append("\n");

        codeBlock.apply(this);

        jasminString.append("\treturn\n" +
                "\t.end method\n" + "\n");

        currentMethod = null;
    }

    @Override
    public void caseAAssignmentStatementAbstract(AAssignmentStatementAbstract node) {
        TIdentifier identifier = node.getIdentifier();
        PExpressionAbstract value = node.getValue();

        value.apply(this);

        currentType = symbolTable.get_var(currentMethod, identifier.getText());

        check_for_typecast();

        int currentIdentifierNum = varsOnStack.get(identifier.getText());

        jasminString.append("\t");

        if (currentType == INTEGER || currentType == BOOLEAN) jasminString.append("i");
        else if(currentType == DOUBLE) jasminString.append("d");
        else if(currentType == STRING) jasminString.append("a");

        jasminString.append("store ").append(currentIdentifierNum).append("\n");
        topStackPeek.pop();
    }

    @Override
    public void caseAReturnStatementAbstract(AReturnStatementAbstract node) {
        LinkedList<PExpressionAbstract> returnValues = node.getReturnValues();

        currentType = symbolTable.get_method_return_type(currentMethod);

        for (PExpressionAbstract rv : returnValues) rv.apply(this);

        jasminString.append("\t");

        switch (currentType) {
            case INTEGER:
            case BOOLEAN:
                jasminString.append("i");
                break;
            case DOUBLE:
                jasminString.append("d");
                break;
            case STRING:
                jasminString.append("a");
                break;
        }

        jasminString.append("return\n");
        currentType = null;
    }

    // int i;
    @Override
    public void caseADeclStatementStatementAbstract(ADeclStatementStatementAbstract node) {
        super.caseADeclStatementStatementAbstract(node);
    }

    // int x = 3;
    @Override
    public void caseAInitStatementStatementAbstract(AInitStatementStatementAbstract node) {
        PTypeAbstract type = node.getType();
        TIdentifier identifier = node.getIdentifier();
        PExpressionAbstract expression = node.getExpression();

        type.apply(this);
        expression.apply(this);

        int currentIdentifierNum = varsOnStack.get(identifier.getText());

        check_for_typecast();

        jasminString.append("\t");

        if (currentType == INTEGER || currentType == BOOLEAN) jasminString.append("i");
        else if (currentType == DOUBLE) jasminString.append("d");
        else if (currentType == STRING) jasminString.append("a");

        jasminString.append("store ").append(currentIdentifierNum).append("\n");
        topStackPeek.pop();

    }

    @Override
    public void caseAWriteLineStatementAbstract(AWriteLineStatementAbstract node) {
        PExpressionAbstract expressionAbstract = node.getExpressionAbstract();

        jasminString.append("\tgetstatic java/lang/System/out Ljava/io/PrintStream;\n");

        expressionAbstract.apply(this);

        // convert boolean value on top to printable value 'false' / 'true'
        if (topStackPeek.peek() == BOOLEAN) swap_int_and_bool_on_top_of_stack();

        jasminString.append("\tinvokevirtual java/io/PrintStream/println(");
        if (topStackPeek.peek() == STRING || topStackPeek.peek() == BOOLEAN) jasminString.append("Ljava/lang/String;");
        else if (topStackPeek.peek() == INTEGER) jasminString.append("I");
        jasminString.append(")V\n");

        topStackPeek.pop();
    }

    @Override
    public void caseAIfStatementAbstract(AIfStatementAbstract node) {
        super.caseAIfStatementAbstract(node);
    }

    @Override
    public void caseAIfElseStatementAbstract(AIfElseStatementAbstract node) {
        super.caseAIfElseStatementAbstract(node);
    }

    @Override
    public void caseAIfElseNoShortIfStatementAbstract(AIfElseNoShortIfStatementAbstract node) {
        super.caseAIfElseNoShortIfStatementAbstract(node);
    }

    @Override
    public void caseAWhileStatementAbstract(AWhileStatementAbstract node) {
        super.caseAWhileStatementAbstract(node);
    }

    @Override
    public void caseAWhileNoShortIfStatementAbstract(AWhileNoShortIfStatementAbstract node) {
        super.caseAWhileNoShortIfStatementAbstract(node);
    }

    @Override
    public void caseACodeBlockStatementAbstract(ACodeBlockStatementAbstract node) {
        super.caseACodeBlockStatementAbstract(node);
    }

    @Override
    public void caseABoolLiteralAbstract(ABoolLiteralAbstract node) {
        jasminString.append("\ticonst_");
        String bool_val = node.getBool().getText();
        if (bool_val.equals("false")) jasminString.append("0\n");
        else jasminString.append("1\n");
        topStackPeek.push(BOOLEAN);
    }

    @Override
    public void caseAIntLiteralAbstract(AIntLiteralAbstract node) {
        jasminString.append("\tldc ").append(node.getIntValue()).append("\n");
        topStackPeek.push(INTEGER);
    }

    @Override
    public void caseADoubleLiteralAbstract(ADoubleLiteralAbstract node) {
        jasminString.append("\tldc2_w ").append(node.getDoubleValue()).append("\n");
        topStackPeek.push(DOUBLE);
    }

    @Override
    public void caseAStringLiteralAbstract(AStringLiteralAbstract node) {
        jasminString.append("\tldc ").append(node.getStringLiteral()).append("\n");
        topStackPeek.push(STRING);
    }

    @Override
    public void caseAIntTypeAbstract(AIntTypeAbstract node) {
        currentType = INTEGER;
    }

    @Override
    public void caseADoubleTypeAbstract(ADoubleTypeAbstract node) {
        currentType = DOUBLE;
    }

    @Override
    public void caseABoolTypeAbstract(ABoolTypeAbstract node) {
        currentType = BOOLEAN;
    }

    @Override
    public void caseAStringTypeAbstract(AStringTypeAbstract node) {
        currentType = STRING;
    }

    @Override
    public void caseAInvokeExpressionAbstract(AInvokeExpressionAbstract node) {
        super.caseAInvokeExpressionAbstract(node);
    }

    @Override
    public void caseAIdentifierExpressionAbstract(AIdentifierExpressionAbstract node) {
        TIdentifier identifier = node.getIdentifier();
        String var_name = identifier.getText();
        Type var_type = symbolTable.getMethodInfos().get(currentMethod).get_var(var_name);

        topStackPeek.push(var_type);

        jasminString.append("\t");

        switch(var_type) {
            case INTEGER:
            case BOOLEAN:
                jasminString.append("i");
                break;
            case DOUBLE:
                jasminString.append("d");
                break;
            case STRING:
                jasminString.append("a");
                break;
        }
        int currentIdentifierNum = varsOnStack.get(identifier.getText());

        jasminString.append("load ").append(currentIdentifierNum).append("\n");

    }

    @Override
    public void caseALiteralExpressionAbstract(ALiteralExpressionAbstract node) {
        node.getLiteralAbstract().apply(this);

    }

    @Override
    public void caseAUnaryPlusExpressionAbstract(AUnaryPlusExpressionAbstract node) {
        super.caseAUnaryPlusExpressionAbstract(node);
    }

    @Override
    public void caseAUnaryMinusExpressionAbstract(AUnaryMinusExpressionAbstract node) {
        PExpressionAbstract expressionAbstract = node.getExpressionAbstract();

        expressionAbstract.apply(this);

        if (topStackPeek.peek() == INTEGER) jasminString.append("\ti");
        else if (topStackPeek.peek() == DOUBLE) jasminString.append("\td");

        jasminString.append("neg\n");
    }

    @Override
    public void caseANotExpressionAbstract(ANotExpressionAbstract node) {
        PExpressionAbstract expressionAbstract = node.getExpressionAbstract();

        expressionAbstract.apply(this);

        jasminString.append("\tifeq TrueToFalse").append(branchCounter).append("\n" +
                "" + "\ticonst_0\n" +
                "\tgoto L").append(branchCounter).append("\n" +
                "\tTrueToFalse").append(branchCounter).append(":\n" +
                "\ticonst_1\n" +
                "\tL").append(branchCounter).append(":\n");

        branchCounter++;
    }

    @Override
    public void caseAMulExpressionAbstract(AMulExpressionAbstract node) {
        PExpressionAbstract left = node.getLeft();
        PExpressionAbstract right = node.getRight();

        left.apply(this);
        check_for_typecast();
        right.apply(this);
        check_for_typecast();

        jasminString.append("\t");

        if (currentType == INTEGER) jasminString.append("i");
        else if (currentType == DOUBLE) jasminString.append("d");

        jasminString.append("mul\n");
    }

    @Override
    public void caseADivExpressionAbstract(ADivExpressionAbstract node) {
        PExpressionAbstract left = node.getLeft();
        PExpressionAbstract right = node.getRight();

        left.apply(this);
        check_for_typecast();
        right.apply(this);
        check_for_typecast();

        jasminString.append("\t");

        if (currentType == INTEGER) jasminString.append("i");
        else if (currentType == DOUBLE) jasminString.append("d");

        jasminString.append("div\n");
    }

    @Override
    public void caseAModExpressionAbstract(AModExpressionAbstract node) {
        super.caseAModExpressionAbstract(node);
        PExpressionAbstract left = node.getLeft();
        PExpressionAbstract right = node.getRight();

        left.apply(this);
        check_for_typecast();
        right.apply(this);
        check_for_typecast();

        jasminString.append("\t");

        if (currentType == INTEGER) jasminString.append("i");
        else if (currentType == DOUBLE) jasminString.append("d");

        jasminString.append("rem\n");
    }

    @Override
    public void caseAPlusExpressionAbstract(APlusExpressionAbstract node) {
        PExpressionAbstract left = node.getLeft();
        PExpressionAbstract right = node.getRight();

        left.apply(this);
        check_for_typecast();
        right.apply(this);
        check_for_typecast();

        jasminString.append("\t");

        if (currentType == INTEGER) jasminString.append("i");
        else if (currentType == DOUBLE) jasminString.append("d");

        jasminString.append("add\n");
    }

    @Override
    public void caseAMinusExpressionAbstract(AMinusExpressionAbstract node) {
        PExpressionAbstract left = node.getLeft();
        PExpressionAbstract right = node.getRight();

        left.apply(this);
        right.apply(this);

        jasminString.append("\t");

        if (currentType == INTEGER) jasminString.append("i");
        else if (currentType == DOUBLE) jasminString.append("d");

        jasminString.append("add\n");
    }

    @Override
    public void caseALtExpressionAbstract(ALtExpressionAbstract node) {
        PExpressionAbstract left = node.getLeft();
        PExpressionAbstract right = node.getRight();

        left.apply(this);
        if (topStackPeek.peek() == INTEGER) {
            jasminString.append("\ti2d\n");
            topStackPeek.pop();
            topStackPeek.push(DOUBLE);
        }
        right.apply(this);
        if (topStackPeek.peek() == INTEGER) {
            jasminString.append("\ti2d\n");
            topStackPeek.pop();
            topStackPeek.push(DOUBLE);
        }

        jasminString.append("\tdcmpg\n" +
                "\tiflt Else").append(branchCounter).append("\n" +
                "" + "\ticonst_0\n" +
                "\tgoto L").append(branchCounter).append("\n" +
                "\tElse").append(branchCounter).append(":\n" +
                "\ticonst_1\n" +
                "\tL").append(branchCounter).append(":\n");

        branchCounter++;

        topStackPeek.pop();
        topStackPeek.pop();
        topStackPeek.push(BOOLEAN);

    }

    @Override
    public void caseAGtExpressionAbstract(AGtExpressionAbstract node) {
        PExpressionAbstract left = node.getLeft();
        PExpressionAbstract right = node.getRight();

        left.apply(this);
        if (topStackPeek.peek() == INTEGER) {
            jasminString.append("\ti2d\n");
            topStackPeek.pop();
            topStackPeek.push(DOUBLE);
        }
        right.apply(this);
        if (topStackPeek.peek() == INTEGER) {
            jasminString.append("\ti2d\n");
            topStackPeek.pop();
            topStackPeek.push(DOUBLE);
        }

        jasminString.append("\tdcmpg\n" +
                "\tifgt Else").append(branchCounter).append("\n" +
                "" + "\ticonst_0\n" +
                "\tgoto L").append(branchCounter).append("\n" +
                "\tElse").append(branchCounter).append(":\n" +
                "\ticonst_1\n" +
                "\tL").append(branchCounter).append(":\n");

        branchCounter++;

        topStackPeek.pop();
        topStackPeek.pop();
        topStackPeek.push(BOOLEAN);
    }

    @Override
    public void caseALteqExpressionAbstract(ALteqExpressionAbstract node) {
        PExpressionAbstract left = node.getLeft();
        PExpressionAbstract right = node.getRight();

        left.apply(this);
        if (topStackPeek.peek() == INTEGER) {
            jasminString.append("\ti2d\n");
            topStackPeek.pop();
            topStackPeek.push(DOUBLE);
        }
        right.apply(this);
        if (topStackPeek.peek() == INTEGER) {
            jasminString.append("\ti2d\n");
            topStackPeek.pop();
            topStackPeek.push(DOUBLE);
        }

        jasminString.append("\tdcmpg\n" +
                "\tifle Else").append(branchCounter).append("\n" +
                "" + "\ticonst_0\n" +
                "\tgoto L").append(branchCounter).append("\n" +
                "\tElse").append(branchCounter).append(":\n" +
                "\ticonst_1\n" +
                "\tL").append(branchCounter).append(":\n");

        branchCounter++;

        topStackPeek.pop();
        topStackPeek.pop();
        topStackPeek.push(BOOLEAN);
    }

    @Override
    public void caseAGteqExpressionAbstract(AGteqExpressionAbstract node) {
        PExpressionAbstract left = node.getLeft();
        PExpressionAbstract right = node.getRight();

        left.apply(this);
        if (topStackPeek.peek() == INTEGER) {
            jasminString.append("\ti2d\n");
            topStackPeek.pop();
            topStackPeek.push(DOUBLE);
        }
        right.apply(this);
        if (topStackPeek.peek() == INTEGER) {
            jasminString.append("\ti2d\n");
            topStackPeek.pop();
            topStackPeek.push(DOUBLE);
        }

        jasminString.append("\tdcmpg\n" +
                "\tifge Else").append(branchCounter).append("\n" +
                "" + "\ticonst_0\n" +
                "\tgoto L").append(branchCounter).append("\n" +
                "\tElse").append(branchCounter).append(":\n" +
                "\ticonst_1\n" +
                "\tL").append(branchCounter).append(":\n");

        branchCounter++;

        topStackPeek.pop();
        topStackPeek.pop();
        topStackPeek.push(BOOLEAN);
    }

    @Override
    public void caseAEqualsExpressionAbstract(AEqualsExpressionAbstract node) {
        //TODO:
        //  check for strings and booleans

        PExpressionAbstract left = node.getLeft();
        PExpressionAbstract right = node.getRight();

        left.apply(this);
        if (topStackPeek.peek() == INTEGER) {
            jasminString.append("\ti2d\n");
            topStackPeek.pop();
            topStackPeek.push(DOUBLE);
        }
        right.apply(this);
        if (topStackPeek.peek() == INTEGER) {
            jasminString.append("\ti2d\n");
            topStackPeek.pop();
            topStackPeek.push(DOUBLE);
        }

        jasminString.append("\tdcmpg\n" +
                "\tifeq Else").append(branchCounter).append("\n" +
                "" + "\ticonst_0\n" +
                "\tgoto L").append(branchCounter).append("\n" +
                "\tElse").append(branchCounter).append(":\n" +
                "\ticonst_1\n" +
                "\tL").append(branchCounter).append(":\n");

        branchCounter++;

        topStackPeek.pop();
        topStackPeek.pop();
        topStackPeek.push(BOOLEAN);
    }

    @Override
    public void caseANotEqualsExpressionAbstract(ANotEqualsExpressionAbstract node) {
        //TODO:
        //  check for strings and booleans

        PExpressionAbstract left = node.getLeft();
        PExpressionAbstract right = node.getRight();

        left.apply(this);
        if (topStackPeek.peek() == INTEGER) {
            jasminString.append("\ti2d\n");
            topStackPeek.pop();
            topStackPeek.push(DOUBLE);
        }
        right.apply(this);
        if (topStackPeek.peek() == INTEGER) {
            jasminString.append("\ti2d\n");
            topStackPeek.pop();
            topStackPeek.push(DOUBLE);
        }

        jasminString.append("\tdcmpg\n" +
                "\tifne Else").append(branchCounter).append("\n" +
                "" + "\ticonst_0\n" +
                "\tgoto L").append(branchCounter).append("\n" +
                "\tElse").append(branchCounter).append(":\n" +
                "\ticonst_1\n" +
                "\tL").append(branchCounter).append(":\n");

        branchCounter++;

        topStackPeek.pop();
        topStackPeek.pop();
        topStackPeek.push(BOOLEAN);    }

    @Override
    public void caseAAndExpressionAbstract(AAndExpressionAbstract node) {
        PExpressionAbstract left = node.getLeft();
        PExpressionAbstract right = node.getRight();

        left.apply(this);
        right.apply(this);

        jasminString.append("\tiand\n");

        topStackPeek.pop();
        topStackPeek.pop();
        topStackPeek.push(BOOLEAN);
    }

    @Override
    public void caseAOrExpressionAbstract(AOrExpressionAbstract node) {
        PExpressionAbstract left = node.getLeft();
        PExpressionAbstract right = node.getRight();

        left.apply(this);
        right.apply(this);

        jasminString.append("\tior\n");

        topStackPeek.pop();
        topStackPeek.pop();
        topStackPeek.push(BOOLEAN);
    }

    @Override
    public void caseAListArgumentListAbstract(AListArgumentListAbstract node) {
        super.caseAListArgumentListAbstract(node);
    }

    @Override
    public void caseAListParameterListAbstract(AListParameterListAbstract node) {
        super.caseAListParameterListAbstract(node);
    }

    @Override
    public void caseAParamParameterAbstract(AParamParameterAbstract node) {
        super.caseAParamParameterAbstract(node);
    }

//--------------------------------------------------------------------------------------------------------------------//
// HELPING METHOD SECTION
//--------------------------------------------------------------------------------------------------------------------//

    // reset stack counter and get all method vars
    private void start_new_method(String method_name) {

        // reserve register0 vor 'String[] args' from main
        if (method_name.equals("Main")) stackCounter = 1;
        // if not main, start register count from 0
        else stackCounter = 0;

        // if branches from if-else / while / cmp will occur --> counter for branches
        // eg: if --> else0; if --> else1...
        branchCounter = 0;

        varsOnStack = new HashMap<>();
        for (String var_name : method_vars.get(method_name)) {
            varsOnStack.put(var_name, stackCounter);
            stackCounter++;
        }
    }

    // check for typecast:
    // is value on top of stack int and var in stack double? -> i2d
    // is value on top of stack double and var in stack int? -> d2i
    private void check_for_typecast() {
        if (topStackPeek.peek() == INTEGER && currentType == DOUBLE) {
            jasminString.append("\ti2d\n");
            topStackPeek.pop();
            topStackPeek.push(DOUBLE);
        }
        else if (topStackPeek.peek() == DOUBLE && currentType == INTEGER) {
            jasminString.append("\td2i\n");
            topStackPeek.pop();
            topStackPeek.push(INTEGER);
        }
    }

    // when bool val is used, then swap Integer and String on top of stack:
    // 0 <--> 'false'
    // 1 <--> 'true'
    private void swap_int_and_bool_on_top_of_stack() {
        jasminString.append("\tifeq UseFalse").append(stackCounter).append("\n" +
                "\tldc \"true\"\n" +
                "\tgoto L").append(stackCounter).append("\n" +
                "\tUseFalse").append(stackCounter).append(":\n" +
                "\tldc \"false\"\n" +
                "\tL").append(stackCounter).append(":\n");
        stackCounter++;
        topStackPeek.pop();
        topStackPeek.push(BOOLEAN);
    }


}