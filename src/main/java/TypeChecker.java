import analysis.ReversedDepthFirstAdapter;
import node.*;

import java.util.LinkedList;
import java.util.List;


public class TypeChecker extends ReversedDepthFirstAdapter {

    private static final Type INTEGER = Type.INTEGER;
    private static final Type DOUBLE = Type.DOUBLE;
    private static final Type BOOLEAN = Type.BOOLEAN;
    private static final Type STRING = Type.STRING;

    private Type currentType;
    private String currentMethod;
    private Boolean comparing = false;

    private final SymbolTable symbolTable;
    private String called_method = "";


    public TypeChecker(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    private Type get_var_type(PTypeAbstract node) {
        node.apply(this);
        if (currentType == INTEGER) return INTEGER;
        if (currentType == DOUBLE) return DOUBLE;
        if (currentType == BOOLEAN) return BOOLEAN;
        if (currentType == STRING) return STRING;

        return null;
    }

    private void resetType() {
        currentType = null;
    }

    private boolean comparable(PExpressionAbstract left, PExpressionAbstract right) {
        comparing = true;

        left.apply(this);
        Type l1 = currentType;
        right.apply(this);
        Type r1 = currentType;

        comparing = false;

        if((l1 == INTEGER && r1 == DOUBLE) ||(l1 == DOUBLE && r1 == INTEGER)) return true;

        return l1 == r1;
    }

    @Override
    public void caseAProgramStartAbstract(AProgramStartAbstract node) {
        List<PMethodDeclarationAbstract> nonMains1 = node.getNonMainStuff1();
        PMainMethodAbstract main = node.getMainMethod();
        List<PMethodDeclarationAbstract> nonMains2 = node.getNonMainStuff2();

        for (PMethodDeclarationAbstract subNode : nonMains1) subNode.apply(this);
        for (PMethodDeclarationAbstract subNode : nonMains2) subNode.apply(this);

        // main last --> invoked methods must be declared before called
        main.apply(this);
    }

    @Override
    public void caseAMainMainMethodAbstract(AMainMainMethodAbstract node) {
        PStatementAbstract sub = node.getStatementAbstract();

        currentMethod = "Main";
        symbolTable.decl_method(currentMethod, Type.VOID);

        sub.apply(this);
    }

    @Override
    public void caseATypeMethodDeclarationAbstract(ATypeMethodDeclarationAbstract node){
        PTypeAbstract type = node.getType();
        LinkedList<PParameterListAbstract> parameterList = node.getParameterList();
        PStatementAbstract codeBlock = node.getCodeBlock();

        type.apply(this);

        // set current method name
        currentMethod = node.getIdentifier().getText();
        symbolTable.decl_method(currentMethod, get_var_type(type));

        // TODO: check each parameter for type ?
        for (PParameterListAbstract param : parameterList) param.apply(this);
        codeBlock.apply(this);
    }

    public void caseAVoidMethodDeclarationAbstract(AVoidMethodDeclarationAbstract node) {
        LinkedList<PParameterListAbstract> parameterList = node.getParameterList();
        PStatementAbstract codeBlock = node.getCodeBlock();

        // set current method name
        currentMethod = node.getIdentifier().getText();
        symbolTable.decl_method(currentMethod, Type.VOID);

        //TODO: check each parameter for type ?
        for (PParameterListAbstract param : parameterList) param.apply(this);
        codeBlock.apply(this);
    }

     @Override
     public void caseAAssignmentStatementAbstract(AAssignmentStatementAbstract node) {
        TIdentifier identifier = node.getIdentifier();
        PExpressionAbstract value = node.getValue();

        identifier.apply(this);

        // TODO: check for Identifiers type
        currentType = symbolTable.get_var(currentMethod, identifier.getText());

        value.apply(this);

        symbolTable.assign_var(currentMethod, identifier.getText());

        resetType();
    }

    @Override
    public void caseAReturnStatementAbstract(AReturnStatementAbstract node) {
        LinkedList<PExpressionAbstract> returnValues = node.getReturnValues();
        currentType = symbolTable.get_method_return_type(currentMethod);

        for (PExpressionAbstract subNode : returnValues){
            subNode.apply(this);
            if (currentType != symbolTable.get_method_return_type(currentMethod))
                throw new TypeCheckerException("Wrong return type " + currentType + " for Method " + currentMethod);
        }

        resetType();
    }

    @Override
    public void caseADeclStatementStatementAbstract(ADeclStatementStatementAbstract node) {

        PTypeAbstract type = node.getType();
        TIdentifier identifier = node.getIdentifier();

        String var_name = identifier.getText();
        Type var_type = get_var_type(type);

        symbolTable.decl_local_variable(currentMethod, var_name, var_type, false);

        type.apply(this);
        identifier.apply(this);

        resetType();
    }

    public void caseAInitStatementStatementAbstract(AInitStatementStatementAbstract node) {

        PTypeAbstract type = node.getType();
        TIdentifier identifier = node.getIdentifier();
        PExpressionAbstract expression = node.getExpression();

        String var_name = identifier.getText();
        Type var_type = get_var_type(type);

        symbolTable.decl_local_variable(currentMethod, var_name, var_type, true);

        type.apply(this);
        identifier.apply(this);

        expression.apply(this);

        resetType();
    }


    @Override
    public void caseAWriteLineStatementAbstract(AWriteLineStatementAbstract node) {
        PExpressionAbstract expressionAbstract = node.getExpressionAbstract();

        expressionAbstract.apply(this);

        resetType();
    }

    @Override
    public void caseAIfStatementAbstract(AIfStatementAbstract node) {
        PExpressionAbstract condition = node.getCondition();
        PStatementAbstract aTrue = node.getTrue();

        condition.apply(this);
        if (!currentType.equals(Type.BOOLEAN)) throw new TypeCheckerException("Expected Type: 'bool'\nactual Type: " + currentType);
        aTrue.apply(this);

    }

    @Override
    public void caseAIfElseStatementAbstract(AIfElseStatementAbstract node) {
        PExpressionAbstract condition = node.getCondition();
        PStatementAbstract aTrue = node.getTrue();
        PStatementAbstract aFalse = node.getFalse();

        condition.apply(this);
        if (!currentType.equals(Type.BOOLEAN)) throw new TypeCheckerException("Expected Type: 'bool'\nactual Type: " + currentType);
        aTrue.apply(this);
        aFalse.apply(this);

    }

    @Override
    public void caseAIfElseNoShortIfStatementAbstract(AIfElseNoShortIfStatementAbstract node) {
        PExpressionAbstract condition = node.getCondition();
        PStatementAbstract aTrue = node.getTrue();
        PStatementAbstract aFalse = node.getFalse();

        condition.apply(this);
        if (!currentType.equals(Type.BOOLEAN)) throw new TypeCheckerException("Expected Type: 'bool'\nactual Type: " + currentType);
        aTrue.apply(this);
        aFalse.apply(this);

    }

    @Override
    public void caseAWhileStatementAbstract(AWhileStatementAbstract node){
        node.getCondition().apply(this);
        if (!currentType.equals(Type.BOOLEAN)) throw new TypeCheckerException("Expected Type: 'bool'\nactual Type: " + currentType);
        node.getTrue().apply(this);

        resetType();
    }

    @Override
    public void caseAWhileNoShortIfStatementAbstract(AWhileNoShortIfStatementAbstract node) {
        node.getCondition().apply(this);
        if (!currentType.equals(Type.BOOLEAN)) throw new TypeCheckerException("Expected Type: 'bool'\nactual Type: " + currentType);
        node.getTrue().apply(this);

        resetType();
    }

    @Override
    public void caseACodeBlockStatementAbstract(ACodeBlockStatementAbstract node) {
        LinkedList<PStatementAbstract> statements = node.getStatementAbstract();
        for (PStatementAbstract stmt : statements) stmt.apply(this);

        resetType();
    }

    @Override
    public void caseABoolLiteralAbstract(ABoolLiteralAbstract node) {
        if (!(currentType == BOOLEAN) && !comparing)
            throw new TypeCheckerException("Expected Type: " + currentType + "\nactual: BOOLEAN");
        currentType = BOOLEAN;
    }

    @Override
    public void caseAIntLiteralAbstract(AIntLiteralAbstract node) {
        if (!(currentType == INTEGER || currentType == DOUBLE) && !comparing)
            throw new TypeCheckerException("Expected Type: " + currentType + "\nactual: INTEGER");
        currentType = INTEGER;
    }

    @Override
    public void caseADoubleLiteralAbstract(ADoubleLiteralAbstract node) {
        if (!(currentType == INTEGER || currentType == DOUBLE) && !comparing)
            throw new TypeCheckerException("Expected Type: " + currentType + "\nactual: DOUBLE");
        currentType = DOUBLE;
    }
    @Override
    public void caseAStringLiteralAbstract(AStringLiteralAbstract node) {
        if (!(currentType == STRING) && !comparing)
            throw new TypeCheckerException("Expected Type: " + currentType + "\nactual: STRING");
        if (comparing) currentType = STRING;

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
        TIdentifier identifier = node.getIdentifier();
        LinkedList<PArgumentListAbstract> arguments = node.getArguments();

        called_method = identifier.getText();
        Type returnType = symbolTable.get_method_return_type(called_method);

        if (currentType != returnType && currentType != null)
            throw new TypeCheckerException("Return Type mismatch: expected: " + currentType + "; actual: " + returnType);

        for (PArgumentListAbstract argument : arguments) argument.apply(this);

    }

    @Override
    public void caseAIdentifierExpressionAbstract(AIdentifierExpressionAbstract node){
        TIdentifier identifier = node.getIdentifier();

        Type id_type = symbolTable.get_var(currentMethod, identifier.getText());

        // check if var is 1) as parameter given or 2) declared and initialised
        // information saved in symbolTable for both
        if (!symbolTable.var_is_init(currentMethod, identifier.getText()))
            throw new TypeCheckerException("Variable '" + identifier.getText() +
                    "' in Method '" + currentMethod +
                    "' only declared, not initialised");

        if (!(((id_type == INTEGER || id_type == DOUBLE) && (currentType == INTEGER || currentType == DOUBLE))
                || (id_type == STRING && currentType == STRING)
                || (id_type == BOOLEAN && currentType == BOOLEAN))) {
            if (!comparing) throw new TypeCheckerException("Identifier: Expected Type: " + currentType +
                       "\nactual: " + id_type);
            else throw new TypeCheckerException("Types not comparable2, can't calculate a boolean");
        }
        identifier.apply(this);
    }

    @Override
    public void caseALiteralExpressionAbstract(ALiteralExpressionAbstract node) {
        PLiteralAbstract literalAbstract = node.getLiteralAbstract();

        literalAbstract.apply(this);
    }

    @Override
    public void caseAUnaryPlusExpressionAbstract(AUnaryPlusExpressionAbstract node) {
        PExpressionAbstract expressionAbstract = node.getExpressionAbstract();

        expressionAbstract.apply(this);
    }

    @Override
    public void caseAUnaryMinusExpressionAbstract(AUnaryMinusExpressionAbstract node) {
        PExpressionAbstract expressionAbstract = node.getExpressionAbstract();

        expressionAbstract.apply(this);
    }

    @Override
    public void caseANotExpressionAbstract(ANotExpressionAbstract node) {
        PExpressionAbstract expressionAbstract = node.getExpressionAbstract();

        currentType = BOOLEAN;

        expressionAbstract.apply(this);

        if (currentType != BOOLEAN) throw new TypeCheckerException( "Expected: BOOLEAN\n" +
                                                                    "actual: " + currentType.toString());
    }


    @Override
    public void caseAMulExpressionAbstract(AMulExpressionAbstract node) {
        PExpressionAbstract left = node.getLeft();
        PExpressionAbstract right = node.getRight();

        boolean isDouble = false;

        left.apply(this);
        if (currentType == DOUBLE) isDouble = true;
        else if (currentType != INTEGER) throw new TypeCheckerException("Expected: INTEGER or DOUBLE; " +
                "actual: " + currentType.toString());
        right.apply(this);
        if (currentType == DOUBLE) isDouble = true;
        else if (currentType != INTEGER) throw new TypeCheckerException("Expected: INTEGER or DOUBLE; " +
                "actual: " + currentType.toString());

        if (isDouble) currentType = DOUBLE;
        else currentType = INTEGER;
    }
    @Override
    public void caseADivExpressionAbstract(ADivExpressionAbstract node) {
        PExpressionAbstract left = node.getLeft();
        PExpressionAbstract right = node.getRight();

        boolean isDouble = false;

        left.apply(this);
        if (currentType == DOUBLE) isDouble = true;
        else if (currentType != INTEGER) throw new TypeCheckerException("Expected: INTEGER or DOUBLE; " +
                                                                        "actual: " + currentType.toString());
        right.apply(this);
        if (currentType == DOUBLE) isDouble = true;
        else if (currentType != INTEGER) throw new TypeCheckerException("Expected: INTEGER or DOUBLE; " +
                "actual: " + currentType.toString());

        if (isDouble) currentType = DOUBLE;
        else currentType = INTEGER;
    }

    @Override
    public void caseAModExpressionAbstract(AModExpressionAbstract node) {
        PExpressionAbstract left = node.getLeft();
        PExpressionAbstract right = node.getRight();

        boolean isDouble = false;

        left.apply(this);
        if (currentType == DOUBLE) isDouble = true;
        else if (currentType != INTEGER) throw new TypeCheckerException("Expected: INTEGER or DOUBLE; " +
                "actual: " + currentType.toString());

        right.apply(this);
        if (currentType == DOUBLE) isDouble = true;
        else if (currentType != INTEGER) throw new TypeCheckerException("Expected: INTEGER or DOUBLE; " +
                "actual: " + currentType.toString());

        if (isDouble) currentType = DOUBLE;
        else currentType = INTEGER;
    }

    @Override
    public void caseAPlusExpressionAbstract(APlusExpressionAbstract node) {
        PExpressionAbstract left = node.getLeft();
        PExpressionAbstract right = node.getRight();

        boolean isDouble = false;
        boolean isString = false;

        left.apply(this);
        if (currentType == DOUBLE) isDouble = true;
        else if (currentType == STRING) isString = true;
        else if (currentType != INTEGER) throw new TypeCheckerException("Expected: INTEGER, DOUBLE or STRING; " +
                "actual: " + currentType.toString());

        right.apply(this);

        if (currentType == DOUBLE) isDouble = true;
        // accept concat: STRING + STRING
        else if (isString && currentType == STRING) {
            currentType = STRING;
            return;
        }
        else if (currentType != INTEGER) throw new TypeCheckerException("Expected: INTEGER, DOUBLE or STRING; " +
                "actual: " + currentType.toString());

        if (isDouble) currentType = DOUBLE;
        else currentType = INTEGER;
    }

    @Override
    public void caseAMinusExpressionAbstract(AMinusExpressionAbstract node) {
        PExpressionAbstract left = node.getLeft();
        PExpressionAbstract right = node.getRight();

        boolean isDouble = false;

        left.apply(this);
        if (currentType == DOUBLE) isDouble = true;
        else if (currentType != INTEGER) throw new TypeCheckerException("Expected: INTEGER or DOUBLE; " +
                "actual: " + currentType.toString());

        right.apply(this);
        if (currentType == DOUBLE) isDouble = true;
        else if (currentType != INTEGER) throw new TypeCheckerException("Expected: INTEGER or DOUBLE; " +
                "actual: " + currentType.toString());

        if (isDouble) currentType = DOUBLE;
        else currentType = INTEGER;
    }

    @Override
    public void caseALtExpressionAbstract(ALtExpressionAbstract node) {
        PExpressionAbstract left = node.getLeft();
        PExpressionAbstract right = node.getRight();

        // for BOOLEAN: '3 < 5' expects number, but currentType would be BOOLEAN
        // --> later revert
        currentType = INTEGER;

        left.apply(this);

        if (!(currentType == INTEGER || currentType == DOUBLE)) throw new TypeCheckerException("Expected: INTEGER or DOUBLE; " +
                "actual: " + currentType.toString());

        right.apply(this);
        if (!(currentType == INTEGER || currentType == DOUBLE)) throw new TypeCheckerException("Expected: INTEGER or DOUBLE; " +
                "actual: " + currentType.toString());

        // revert
        currentType = BOOLEAN;
    }

    @Override
    public void caseAGtExpressionAbstract(AGtExpressionAbstract node) {
        PExpressionAbstract left = node.getLeft();
        PExpressionAbstract right = node.getRight();

        // for BOOLEAN: '3 < 5' expects number, but currentType would be BOOLEAN
        // --> later revert
        currentType = INTEGER;

        left.apply(this);

        if (!(currentType == INTEGER || currentType == DOUBLE)) throw new TypeCheckerException("Expected: INTEGER or DOUBLE; " +
                "actual: " + currentType.toString());

        right.apply(this);
        if (!(currentType == INTEGER || currentType == DOUBLE)) throw new TypeCheckerException("Expected: INTEGER or DOUBLE; " +
                "actual: " + currentType.toString());

        // revert
        currentType = BOOLEAN;
    }



    @Override
    public void caseALteqExpressionAbstract(ALteqExpressionAbstract node) {
        PExpressionAbstract left = node.getLeft();
        PExpressionAbstract right = node.getRight();

        // for BOOLEAN: '3 < 5' expects number, but currentType would be BOOLEAN
        // --> later revert
        currentType = INTEGER;

        left.apply(this);

        if (!(currentType == INTEGER || currentType == DOUBLE)) throw new TypeCheckerException("Expected: INTEGER or DOUBLE; " +
                "actual: " + currentType.toString());

        right.apply(this);
        if (!(currentType == INTEGER || currentType == DOUBLE)) throw new TypeCheckerException("Expected: INTEGER or DOUBLE; " +
                "actual: " + currentType.toString());

        // revert
        currentType = BOOLEAN;
    }

    @Override
    public void caseAGteqExpressionAbstract(AGteqExpressionAbstract node) {
        PExpressionAbstract left = node.getLeft();
        PExpressionAbstract right = node.getRight();

        // for BOOLEAN: '3 < 5' expects number, but currentType would be BOOLEAN
        // --> later revert
        currentType = INTEGER;

        left.apply(this);

        if (!(currentType == INTEGER || currentType == DOUBLE)) throw new TypeCheckerException("Expected: INTEGER or DOUBLE; " +
                "actual: " + currentType.toString());

        right.apply(this);
        if (!(currentType == INTEGER || currentType == DOUBLE)) throw new TypeCheckerException("Expected: INTEGER or DOUBLE; " +
                "actual: " + currentType.toString());

        // revert
        currentType = BOOLEAN;
    }

    @Override
    public void caseAEqualsExpressionAbstract(AEqualsExpressionAbstract node) {
        PExpressionAbstract left = node.getLeft();
        PExpressionAbstract right = node.getRight();

//        boolean isIntegerOrDouble = false;
//        boolean isString = false;
//        boolean isBoolean = false;

        if(!comparable(left, right))
            throw new TypeCheckerException("Types not comparable, can't calculate a boolean");
        /*
        left.apply(this);

        if (currentType == INTEGER || currentType == DOUBLE) isIntegerOrDouble = true;
        else if (currentType == BOOLEAN) isBoolean = true;
        else if (currentType == STRING) isString = true;

        right.apply(this);
        if ((currentType == INTEGER || currentType == DOUBLE) && isIntegerOrDouble) currentType = BOOLEAN;
        else if (currentType == STRING && isString) currentType = BOOLEAN;
        else if (currentType == BOOLEAN && isBoolean) currentType = BOOLEAN;
        else {
            String m = "";
            if (isBoolean) m = "BOOLEAN";
            else if (isIntegerOrDouble) m = "INTEGER or DOUBLE";
            else if (isString) m = "STRING";

            throw new TypeCheckerException( "'==' expected: " + m + "; " +
                                            "actual: " + currentType.toString());

        }
        */
    }

    @Override
    public void caseANotEqualsExpressionAbstract(ANotEqualsExpressionAbstract node) {
        PExpressionAbstract left = node.getLeft();
        PExpressionAbstract right = node.getRight();

//        boolean isIntegerOrDouble = false;
//        boolean isString = false;
//        boolean isBoolean = false;

        if(!comparable(left, right))
            throw new TypeCheckerException("Types not comparable, can't calculate a boolean");

    }

    @Override
    public void caseAAndExpressionAbstract(AAndExpressionAbstract node) {
        PExpressionAbstract left = node.getLeft();
        PExpressionAbstract right = node.getRight();

        left.apply(this);
        if (currentType != BOOLEAN) throw new TypeCheckerException( "'&&' expected: BOOLEAN; " +
                                                                    "actual: " + currentType.toString());
        right.apply(this);
        if (currentType != BOOLEAN) throw new TypeCheckerException( "'&&' expected: BOOLEAN; " +
                                                                    "actual: " + currentType.toString());
        currentType = BOOLEAN;
    }

    @Override
    public void caseAOrExpressionAbstract(AOrExpressionAbstract node) {
        PExpressionAbstract left = node.getLeft();
        PExpressionAbstract right = node.getRight();

        left.apply(this);
        if (currentType != BOOLEAN) throw new TypeCheckerException( "'||' expected: BOOLEAN; " +
                "actual: " + currentType.toString());
        right.apply(this);
        if (currentType != BOOLEAN) throw new TypeCheckerException( "'||' expected: BOOLEAN; " +
                "actual: " + currentType.toString());
        currentType = BOOLEAN;
    }


    // arguments given for invoked method
    @Override
    public void caseAListArgumentListAbstract(AListArgumentListAbstract node) {
        PExpressionAbstract first = node.getFirst();
        LinkedList<PExpressionAbstract> follow = node.getFollow();

        int counter = 0;
        List<Type> params = symbolTable.get_params(called_method);

        if (params.size() == 0) throw new TypeCheckerException("Method '" + called_method + "' called with too few arguments.");

        currentType = params.get(counter);
        counter++;

        first.apply(this);

        for (PExpressionAbstract f : follow) {
            if (counter >= params.size()) throw new TypeCheckerException("Method '" + called_method + "' called with too many arguments.");
            currentType = params.get(counter);
            f.apply(this);

            counter++;
        }

        if (counter < params.size()) throw new TypeCheckerException("Method '" + called_method + "' called with too few arguments.");

        called_method = "";
    }

    @Override
    public void caseAListParameterListAbstract(AListParameterListAbstract node) {
        PParameterAbstract first = node.getFirst();
        LinkedList<PParameterAbstract> follow = node.getFollow();

        first.apply(this);

        for (PParameterAbstract f : follow) f.apply(this);
    }

    @Override
    public void caseAParamParameterAbstract(AParamParameterAbstract node){
        PTypeAbstract type = node.getType();
        TIdentifier identifier = node.getIdentifier();

        type.apply(this);

        symbolTable.decl_param(currentMethod, identifier.getText(), get_var_type(type));

        identifier.apply(this);

    }

}
