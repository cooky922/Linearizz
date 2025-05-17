package ccc102.linearizz.system;

import java.util.Collection;
import java.util.Set;
import java.util.LinkedHashSet;
import ccc102.linearizz.exceptions.VariableException;

public class Variables {
    // use linked hash set to preserve the order of variables
    // respect to insertion order
    private Set<String> variableSet = new LinkedHashSet<>();

    // get variable names in set
    public Set<String> getNameSet() {
        return variableSet;
    }

    // get variable names in array
    public String[] getNames() {
        return variableSet.toArray(String[]::new);
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
        return variableSet.contains(name);
    }

    public void add(String name) throws VariableException {
        if (name.isEmpty())
            throw new VariableException(VariableException.Kind.EmptyString);
        if (!isValidName(name))
            throw new VariableException(VariableException.Kind.InvalidName);
        if (variableSet.contains(name))
            throw new VariableException(VariableException.Kind.AlreadyExisted);
        variableSet.add(name);
    }

    // same as 'add' but it does no effect when the variable 'name' already exists
    public void addIfNew(String name) throws VariableException {
        if (name.isEmpty())
            throw new VariableException(VariableException.Kind.EmptyString);
        if (!isValidName(name))
            throw new VariableException(VariableException.Kind.InvalidName);
        if (!variableSet.contains(name))
            variableSet.add(name);
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
    public void addAllIfNew(String[] c) throws VariableException {
        for (String name : c)
            addIfNew(name);
    }

    public void addAllIfNew(Iterable<? extends String> c) throws VariableException {
        for (String name : c)
            addIfNew(name);
    }

    public void addAllIfNew(Variables other) throws VariableException {
        addAllIfNew(other.getNameSet());
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
