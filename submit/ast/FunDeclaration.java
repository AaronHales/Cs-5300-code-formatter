package submit.ast;


public class FunDeclaration implements Declaration, Node{

    private final VarType type;
    private final FunctionID id;
    private final boolean isStatic;

    public FunDeclaration(VarType type, FunctionID id, boolean isStatic) {
        this.type = type;
        this.id = id;
        this.isStatic = isStatic;
    }

    @Override
    public void toCminus(StringBuilder builder, String prefix) {
        builder.append(prefix);
        if (isStatic) {
            builder.append("static ");
        }
        builder.append(type).append(" ");
        id.toCminus(builder, "");
    }
}
