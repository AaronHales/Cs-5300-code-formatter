package submit.ast;

import java.util.ArrayList;
import java.util.List;

public class Function implements Node, Declaration{


    private FunDeclaration funDecl;
    private ArrayList<Param> params;
    private ArrayList<Statement> declarations;

    public Function(FunDeclaration funDecl, List<Param> params, List<Statement> declarations) {
        this.funDecl = funDecl;
        this.params = new ArrayList<>(params);
        this.declarations = new ArrayList<>(declarations);
    }

    @Override
    public void toCminus(StringBuilder builder, String prefix) {
        builder.append("\n");
        funDecl.toCminus(builder, "");
        builder.append(String.format("("));
        for (Param param : params) {
            param.toCminus(builder, "");
            builder.append(", ");
        }
        if (params.size() > 0) {
            builder.delete(builder.length() - 2, builder.length());
        }
        builder.append(")\n{");
        for (Statement declaration : declarations) {
            declaration.toCminus(builder, "");
        }
        builder.append("}");
    }
}
