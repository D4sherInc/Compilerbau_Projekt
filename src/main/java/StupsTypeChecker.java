import exceptions.SymbolTableException;
import exceptions.TypeCheckerException;
import node.Start;

// self written class, called by StupsCompiler
// extra class for testing
public class StupsTypeChecker {

    private Start tree;
    // extended class from ReversedDepthFirstAdapter
    private TypeChecker typeChecker;


    public StupsTypeChecker(Start tree) {
        this.tree = tree;
        SymbolTable symbolTable = new SymbolTable();
        this.typeChecker = new TypeChecker(symbolTable);
    }


    public void typechecking() {

        System.out.println("start typechecking...");
        try {
            tree.apply(typeChecker);
        } catch (TypeCheckerException | SymbolTableException e) {
            throw new TypeCheckerException(String.format("Type ERROR: %s", e.getMessage()));
        }
        System.out.println("typecheck successful!\n");
    }

    public SymbolTable getSymbolTable() {
        return typeChecker.getSymbolTable();
    }
}
