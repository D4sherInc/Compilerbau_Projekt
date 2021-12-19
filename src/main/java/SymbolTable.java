import java.util.HashMap;
import java.util.Map;

public class SymbolTable {

    private final Map<String, MethodInfo> methodInfos = new HashMap<>();

    // declare method
    public void decl_method(final String method_name, final Type method_type) {
        MethodInfo methodInfo = new MethodInfo(method_type);
        if (methodInfos.put(method_name, methodInfo) != null)
            throw new SymbolTableException("Method named: " + method_name + "\nalready declared!");
    }

    // declare variable in method scope
    public void decl_local_variable(final String method_name, final String var_name, final Type var_type, final boolean isInit) {
        MethodInfo methodInfo = methodInfos.get(method_name);
        if (!methodInfo.decl_var(var_name, var_type, isInit))
            throw new SymbolTableException("Method named: "
                                            + method_name
                                            + "\nalready declared a variable named: "
                                            + var_name);
    }

    public void decl_param(final String method_name, final String var_name, final Type var_type) {
        MethodInfo methodInfo = methodInfos.get(method_name);
        if (!methodInfo.decl_param(var_name, var_type))
            throw new SymbolTableException("Method named: "
                                            + method_name
                                            + "\nalready declared a parameter named: "
                                            + var_name);
    }

    // get variable from local method scope if existent
    public Type get_var(final String method_name, final String var_name) {
        Type t = null;

        if (!method_name.isEmpty()) {
            MethodInfo methodInfo = methodInfos.get(method_name);
            t = methodInfo.get_var(var_name);
        }

        if (t == null) {
            throw new SymbolTableException("Variable named: "
                                            + var_name
                                            + "\nnot found");
        }
        return t;
    }

    public void assign_var(String method_name, String var_name) {
        methodInfos.get(method_name).assign_var(var_name);
    }

    public boolean var_is_init(String currentMethod, String var_name) {
        return methodInfos.get(currentMethod).var_is_init(var_name);
    }
}
