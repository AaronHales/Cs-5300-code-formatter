package submit;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import parser.CminusBaseVisitor;
import parser.CminusParser;
import submit.ast.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ASTVisitor extends CminusBaseVisitor<Node> {
    private final Logger LOGGER;
    private SymbolTable symbolTable;

    public ASTVisitor(Logger LOGGER) {
        this.LOGGER = LOGGER;
    }

    private VarType getVarType(CminusParser.TypeSpecifierContext ctx) {
        final String t = ctx.getText();
        return (t.equals("int")) ? VarType.INT : (t.equals("bool")) ? VarType.BOOL : VarType.CHAR;
    }

    @Override
    public Node visitProgram(CminusParser.ProgramContext ctx) {
        symbolTable = new SymbolTable();
        List<Declaration> decls = new ArrayList<>();
        for (CminusParser.DeclarationContext d : ctx.declaration()) {
//            LOGGER.fine(String.format("d: %s", d.getText()));
//            decls.add((Declaration) visitDeclaration(d));
            decls.add((Declaration) visitChildren(d));
        }
        return new Program(decls);
    }

    @Override
    public Node visitVarDeclaration(CminusParser.VarDeclarationContext ctx) {
        for (CminusParser.VarDeclIdContext v : ctx.varDeclId()) {
            String id = v.ID().getText();
            LOGGER.fine("Var ID: " + id);
        }
//        return null;

        VarType type = getVarType(ctx.typeSpecifier());
        List<String> ids = new ArrayList<>();
        List<Integer> arraySizes = new ArrayList<>();
        for (CminusParser.VarDeclIdContext v : ctx.varDeclId()) {
            String id = v.ID().getText();
            ids.add(id);
            symbolTable.addSymbol(id, new SymbolInfo(id, type, false));
            if (v.NUMCONST() != null) {
                arraySizes.add(Integer.parseInt(v.NUMCONST().getText()));
            } else {
                arraySizes.add(-1);
            }
        }
        final boolean isStatic = false;
        return new VarDeclaration(type, ids, arraySizes, isStatic);
    }

    @Override
    public Node visitReturnStmt(CminusParser.ReturnStmtContext ctx) {
        if (ctx.expression() != null) {
            return new Return((Expression) visitExpression(ctx.expression()));
        }
        return new Return(null);
    }

    @Override
    public Node visitConstant(CminusParser.ConstantContext ctx) {
        final Node node;
        if (ctx.NUMCONST() != null) {
            node = new NumConstant(Integer.parseInt(ctx.NUMCONST().getText()));
        } else if (ctx.CHARCONST() != null) {
            node = new CharConstant(ctx.CHARCONST().getText().charAt(0));
        } else if (ctx.STRINGCONST() != null) {
            node = new StringConstant(ctx.STRINGCONST().getText());
        } else {
            node = new BoolConstant(ctx.getText().equals("true"));
        }
        return node;
    }

    // TODO Uncomment and implement whatever methods make sense
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override
    public Node visitDeclaration(CminusParser.DeclarationContext ctx) {
        LOGGER.fine(String.format("visitDeclaration ctx: %s", ctx.getText()));
        return visitChildren(ctx);
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override
    public Node visitVarDeclId(CminusParser.VarDeclIdContext ctx) {
        return visitChildren(ctx);
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override
    public Node visitFunDeclaration(CminusParser.FunDeclarationContext ctx) {
        List<Param> params = new ArrayList<>();
        List<Statement> statements = new ArrayList<>();
        for (CminusParser.ParamContext param : ctx.param()) {
            params.add((Param) visit(param));
//            decls.add((Declaration) visitChildren(d));
        }
        LOGGER.fine(String.format("ctx: %s, id: %s", ctx.getText(), ctx.ID()));
        LOGGER.fine("\tChildern:");
        for (ParseTree children: ctx.children) {
            LOGGER.fine(String.format("\t\ttext: %s, type: %s", children.getText(), children));
            visitChildren(ctx);
//            statements.add((Statement) visitChildren(ctx));
        }
//        for (CminusParser.ParamContext param : ctx.param()) {
//            LOGGER.fine(String.format("\tparam: %s", param.getText()));
//        }
        LOGGER.fine(String.format("\ttypeSpecifier: %s", getVarType(ctx.typeSpecifier())));
        symbolTable.createChild().addSymbol(ctx.ID().getText(), new SymbolInfo(ctx.ID().getText(), getVarType(ctx.typeSpecifier()), true));
        LOGGER.fine(String.format("funParams decls: %s", params.toString()));
        FunDeclaration funDecl = new FunDeclaration(getVarType(ctx.typeSpecifier()), new FunctionID(ctx.ID().getText()), false);
        return new Function(funDecl, params, statements);
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override
    public Node visitTypeSpecifier(CminusParser.TypeSpecifierContext ctx) {
        return visitChildren(ctx);
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override
    public Node visitParam(CminusParser.ParamContext ctx) {
        LOGGER.fine(String.format("param: %s, type: %s, paramID: %s", ctx.getText(), getVarType(ctx.typeSpecifier()), ctx.paramId()));
        return new Param(getVarType(ctx.typeSpecifier()), (ParamID) visitParamId(ctx.paramId()));
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override
    public Node visitParamId(CminusParser.ParamIdContext ctx) {
        LOGGER.fine(String.format("ParamId id: %s, text: %s", ctx.ID(), ctx.getText()));
        for (ParseTree paramId : ctx.children) {
            LOGGER.fine(String.format("\tparamId child: %s", paramId.getText()));
        }
        return new ParamID(ctx.getText());
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override
    public Node visitStatement(CminusParser.StatementContext ctx) {
        LOGGER.fine(String.format("VisitStatement\n\tctx: %s", ctx.getText()));
        return visitChildren(ctx);
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override
    public Node visitCompoundStmt(CminusParser.CompoundStmtContext ctx) {
        LOGGER.fine(String.format("VisitCompoundStmt\n\tctx: %s", ctx.getText()));
        return visitChildren(ctx);
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override
    public Node visitExpressionStmt(CminusParser.ExpressionStmtContext ctx) {
        LOGGER.fine(String.format("VisitExpStmt\n\tctx: %s", ctx.getText()));
        return visitChildren(ctx);
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override
    public Node visitIfStmt(CminusParser.IfStmtContext ctx) {
        return visitChildren(ctx);
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override
    public Node visitWhileStmt(CminusParser.WhileStmtContext ctx) {
        return visitChildren(ctx);
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override
    public Node visitBreakStmt(CminusParser.BreakStmtContext ctx) {
        return visitChildren(ctx);
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override
    public Node visitExpression(CminusParser.ExpressionContext ctx) {
        LOGGER.fine(String.format("VisitExpression\n\tctx: %s", ctx.getText()));
        return visitChildren(ctx);
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override
    public Node visitSimpleExpression(CminusParser.SimpleExpressionContext ctx) {
        LOGGER.fine(String.format("VisitSimpleExpression\n\tctx: %s", ctx.getText()));
        return visitChildren(ctx);
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override
    public Node visitOrExpression(CminusParser.OrExpressionContext ctx) {
        return visitChildren(ctx);
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override
    public Node visitAndExpression(CminusParser.AndExpressionContext ctx) {
        return visitChildren(ctx);
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override
    public Node visitUnaryRelExpression(CminusParser.UnaryRelExpressionContext ctx) {
        LOGGER.fine(String.format("VisitUnaryRelExpression\n\tctx: %s", ctx.getText()));
        return visitChildren(ctx);
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override
    public Node visitRelExpression(CminusParser.RelExpressionContext ctx) {
        return visitChildren(ctx);
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override
    public Node visitRelop(CminusParser.RelopContext ctx) {
        return visitChildren(ctx);
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override
    public Node visitSumExpression(CminusParser.SumExpressionContext ctx) {
        LOGGER.fine(String.format("VisitSumExpression\n\tctx: %s", ctx.getText()));
        return visitChildren(ctx);
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override
    public Node visitSumop(CminusParser.SumopContext ctx) {
        return visitChildren(ctx);
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override
    public Node visitTermExpression(CminusParser.TermExpressionContext ctx) {
        return visitChildren(ctx);
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override
    public Node visitMulop(CminusParser.MulopContext ctx) {
        return visitChildren(ctx);
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override
    public Node visitUnaryExpression(CminusParser.UnaryExpressionContext ctx) {
        return visitChildren(ctx);
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override
    public Node visitUnaryop(CminusParser.UnaryopContext ctx) {
        return visitChildren(ctx);
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override
    public Node visitFactor(CminusParser.FactorContext ctx) {
        return visitChildren(ctx);
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override
    public Node visitMutable(CminusParser.MutableContext ctx) {
        return visitChildren(ctx);
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override
    public Node visitImmutable(CminusParser.ImmutableContext ctx) {
        return visitChildren(ctx);
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override
    public Node visitCall(CminusParser.CallContext ctx) {
        return visitChildren(ctx);
    }

}
