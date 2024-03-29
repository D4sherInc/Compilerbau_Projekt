import java.util.*;
import java.util.stream.Collectors;

public class MethodInfo {

    private final Type return_type;
    private final Map<String, Type> vars = new HashMap<>();
    private final Map<String, Boolean> var_is_init = new HashMap<>();
    private final List<Map.Entry<String, Type>> params = new ArrayList<>();

    public MethodInfo(Type method_type) {
        this.return_type = method_type;
    }

    public Type getType() {
        return return_type;
    }

    // for CodeGenerator: get all var names to put on stack
    public Set<String> get_var_and_param_names() {
        // all params
        Set<String> newCollect = new LinkedHashSet<>();
        for (Map.Entry<String, Type> entry : params) {
            newCollect.add(entry.getKey());
        }
        // Set<String> collect = params.stream().map(Map.Entry::getKey).collect(Collectors.toSet());
        // join all params with all vars
        newCollect.addAll(vars.keySet());
        return newCollect;
    }

    public List<Type> getParams() {
        return params.stream().map(Map.Entry::getValue).collect(Collectors.toList());
    }

    public boolean decl_var(String var_name, Type var_type, boolean isInit) {
        var_is_init.put(var_name, isInit);
        return vars.put(var_name, var_type) == null;
    }


    public boolean decl_param(String var_name, Type var_type) {
        for (var param : params) {
            if (param.getKey().equals(var_name)) return false;
        }
        params.add(new AbstractMap.SimpleEntry<>(var_name, var_type));
        var_is_init.put(var_name, true);
        return true;
    }

    public Type get_var(String var_name) {
        if (vars.containsKey(var_name)) return vars.get(var_name);
        else {
            for (var param : params) {
                if (param.getKey().equals(var_name)) return param.getValue();
            }
        }
        return null;
    }

    public void assign_var(String var_name) {
        var_is_init.put(var_name, true);
    }


    public boolean var_is_init(String var_name) {
        return var_is_init.get(var_name);
    }

    // for Codegen: DOUBLEVALUES need 2 spaces on stack
    // --> check for DOUBLEVALUE
    public Type get_specific_param_type(String var_name) {
        for (Map.Entry<String, Type> entry : params){
            if (entry.getKey().equals(var_name)) return entry.getValue();
        }
        return null;
    }
}