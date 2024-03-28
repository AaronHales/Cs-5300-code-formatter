package submit.ast;


public class Param implements Declaration{

    private final VarType type;
    private final ParamID id;

    public Param(VarType type, ParamID id) {
        this.type = type;
        this.id = id;
    }

    @Override
    public void toCminus(StringBuilder builder, String prefix) {
        builder.append(type).append(" ");
        id.toCminus(builder, "");
    }
}
