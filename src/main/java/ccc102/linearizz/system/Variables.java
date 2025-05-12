package ccc102.linearizz.system;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import ccc102.linearizz.exceptions.VariableException;

public class Variables {
    // each name is mapped to its index (the order of variables)
    private Map<String, Integer> variableSet = new HashMap<>();

    // get variable names in set
    public Set<String> getNameSet() {
        return variableSet.keySet();
    }

    // get variable names in order
    public String[] getNames() {
        String[] result = new String[variableSet.size()];
        for (Map.Entry<String, Integer> entry : variableSet.entrySet())
            result[entry.getValue().intValue()] = entry.getKey();
        return result;
    }

    // clears all variable names
    public void clear() {
        variableSet.clear();
    }

    /// queries
    public boolean isEmpty() {
        return variableSet.isEmpty();
    }

    public int count() {
        return variableSet.size();
    }

    public boolean containsName(String name) {
        return variableSet.containsKey(name);
    }

    public void add(String name) throws VariableException {
        if (name.isEmpty())
            throw new VariableException(VariableException.Kind.EmptyString);
        if (!isValidName(name))
            throw new VariableException(VariableException.Kind.InvalidName);
        final int index = variableSet.size();
        if (variableSet.containsKey(name))
            throw new VariableException(VariableException.Kind.AlreadyExisted);
        variableSet.put(name, index);
    }

    // same as 'add' but it does no effect when the variable 'name' already exists
    public void addNew(String name) throws VariableException {
        if (name.isEmpty())
            throw new VariableException(VariableException.Kind.EmptyString);
        if (!isValidName(name))
            throw new VariableException(VariableException.Kind.InvalidName);
        final int index = variableSet.size();
        if (!variableSet.containsKey(name))
            variableSet.put(name, index);
    }

    public void addAll(String[] c) throws VariableException {
        for (String name : c)
            add(name);
    }

    public void addAll(Iterable<? extends String> c) throws VariableException {
        for (String name : c)
            add(name);
    }

    public void addAll(Variables other) throws VariableException {
        addAll(other.getNameSet());
    }

    // same as 'addAll' but it does no effect when the variable 'name' already exists
    public void addAllNew(String[] c) throws VariableException {
        for (String name : c)
            addNew(name);
    }

    public void addAllNew(Iterable<? extends String> c) throws VariableException {
        for (String name : c)
            addNew(name);
    }

    public void addAllNew(Variables other) throws VariableException {
        addAllNew(other.getNameSet());
    }

    /// HELPERS ...........
    // checks whether the variable name is valid
    public static boolean isValidName(String name) {
        if (!Character.isLetter(name.charAt(0)))
            return false;
        for (int i = 1; i < name.length(); ++i)
            if (!Character.isDigit(name.charAt(i)))
                return false;
        return true;
    }
}
