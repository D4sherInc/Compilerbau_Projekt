// visits parsetree
// generates a .j jasmine file

import analysis.DepthFirstAdapter;
import node.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class CodeGenerator extends DepthFirstAdapter {

    private final File jasmin;
    private final Start tree;
    private StringBuilder jasminString;


    private static final Type INTEGER = Type.INTEGER;
    private static final Type DOUBLE = Type.DOUBLE;
    private static final Type BOOLEAN = Type.BOOLEAN;
    private static final Type STRING = Type.STRING;

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


    public CodeGenerator(SymbolTable symbolTable, Start tree, String filepath) {
        this.jasmin = new File(filepath);
        this.tree = tree;
        this.method_vars = new HashMap<>();

        // add all vars in HashMap
        for (String method_name : symbolTable.getMethodInfos().keySet()) {
            method_vars.put(method_name, symbolTable.get_var_and_param_names(method_name));
        }

        // cut: '/path/to/filename.j' -> 'filename'
        String filename = filepath.substring(filepath.lastIndexOf("/") + 1, filepath.length() - 2);

        generateCode(filename);
    }

    public File getJasmin() {
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
        jasminString.append(
                ".method public static main([Ljava/lang/String;)V\n" +
                "\t.limit stack 255\n" +
                "\t.limit locals " + method_vars.get(currentMethod).size() + "\n");

        statementAbstract.apply(this);

        jasminString.append("\t.end method\n" +
                "\n");

        currentMethod = null;

    }

    @Override
    public void caseATypeMethodDeclarationAbstract(ATypeMethodDeclarationAbstract node) {
        TIdentifier identifier = node.getIdentifier();
        LinkedList<PParameterListAbstract> parameterList = node.getParameterList();
        PStatementAbstract codeBlock = node.getCodeBlock();

        currentMethod = identifier.getText();
        start_new_method(currentMethod);

        codeBlock.apply(this);

        currentMethod = null;

    }


    @Override
    public void caseAVoidMethodDeclarationAbstract(AVoidMethodDeclarationAbstract node) {
        super.caseAVoidMethodDeclarationAbstract(node);
    }

    @Override
    public void caseAAssignmentStatementAbstract(AAssignmentStatementAbstract node) {
        TIdentifier identifier = node.getIdentifier();
        PExpressionAbstract value = node.getValue();

        value.apply(this);

        int currentIdentifierNum = varsOnStack.get(identifier.getText());

        jasminString.append("\t");

        if (currentType == INTEGER) jasminString.append("i");
        else if(currentType == DOUBLE) jasminString.append("d");
        else if(currentType == STRING || currentType == BOOLEAN) jasminString.append("a");



        jasminString.append("store " + currentIdentifierNum + "\n");
    }

    @Override
    public void caseAReturnStatementAbstract(AReturnStatementAbstract node) {
        super.caseAReturnStatementAbstract(node);
    }

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

        jasminString.append("\t");

        if (currentType == INTEGER) jasminString.append("i");
        else if (currentType == DOUBLE) jasminString.append("d");
        else if (currentType == STRING) jasminString.append("a");

        jasminString.append("store " + currentIdentifierNum + "\n");

    }

    @Override
    public void caseAWriteLineStatementAbstract(AWriteLineStatementAbstract node) {
        super.caseAWriteLineStatementAbstract(node);
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
        super.caseABoolLiteralAbstract(node);
    }

    @Override
    public void caseAIntLiteralAbstract(AIntLiteralAbstract node) {
        jasminString.append("\tldc " + node.getIntValue() + "\n");
    }

    @Override
    public void caseADoubleLiteralAbstract(ADoubleLiteralAbstract node) {
        super.caseADoubleLiteralAbstract(node);
    }

    @Override
    public void caseAStringLiteralAbstract(AStringLiteralAbstract node) {
        super.caseAStringLiteralAbstract(node);
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
        super.caseAIdentifierExpressionAbstract(node);
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
        super.caseAUnaryMinusExpressionAbstract(node);
    }

    @Override
    public void caseANotExpressionAbstract(ANotExpressionAbstract node) {
        super.caseANotExpressionAbstract(node);
    }

    @Override
    public void caseAMulExpressionAbstract(AMulExpressionAbstract node) {
        super.caseAMulExpressionAbstract(node);
    }

    @Override
    public void caseADivExpressionAbstract(ADivExpressionAbstract node) {
        super.caseADivExpressionAbstract(node);
    }

    @Override
    public void caseAModExpressionAbstract(AModExpressionAbstract node) {
        super.caseAModExpressionAbstract(node);
    }

    // 3 + 4
    @Override
    public void caseAPlusExpressionAbstract(APlusExpressionAbstract node) {
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
    public void caseAMinusExpressionAbstract(AMinusExpressionAbstract node) {
        super.caseAMinusExpressionAbstract(node);
    }

    @Override
    public void caseALtExpressionAbstract(ALtExpressionAbstract node) {
        super.caseALtExpressionAbstract(node);
    }

    @Override
    public void caseAGtExpressionAbstract(AGtExpressionAbstract node) {
        super.caseAGtExpressionAbstract(node);
    }

    @Override
    public void caseALteqExpressionAbstract(ALteqExpressionAbstract node) {
        super.caseALteqExpressionAbstract(node);
    }

    @Override
    public void caseAGteqExpressionAbstract(AGteqExpressionAbstract node) {
        super.caseAGteqExpressionAbstract(node);
    }

    @Override
    public void caseAEqualsExpressionAbstract(AEqualsExpressionAbstract node) {
        super.caseAEqualsExpressionAbstract(node);
    }

    @Override
    public void caseANotEqualsExpressionAbstract(ANotEqualsExpressionAbstract node) {
        super.caseANotEqualsExpressionAbstract(node);
    }

    @Override
    public void caseAAndExpressionAbstract(AAndExpressionAbstract node) {
        super.caseAAndExpressionAbstract(node);
    }

    @Override
    public void caseAOrExpressionAbstract(AOrExpressionAbstract node) {
        super.caseAOrExpressionAbstract(node);
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

    // helping method to reset stack counter and get all method vars
    private void start_new_method(String method_name) {
        stackCounter = 1;
        varsOnStack = new HashMap<>();
        for (String var_name : method_vars.get(method_name)) {
            varsOnStack.put(var_name, stackCounter);
            stackCounter++;
        }

    }

}
