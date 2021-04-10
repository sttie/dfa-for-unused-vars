package parsing

import ast.*
import lexing.Lexer
import lexing.Token
import lexing.TokenType
import java.util.*

/*
 * program        ::= statement_list
 * statement_list ::= statement | statement_list statement
 * statement      ::= variable '=' expression | 'if' expression statement_list 'end' | 'while' expression statement_list 'end'
 * expression     ::= variable | constant | '(' expression ')' | expression operator expression
 * operator       ::= '+' | '-' | '*' | '/' | '<' | '>'
 *
 * Эта грамматика - леворекурсивна, поэтому написать LL-парсер без костылей здесь не получится,
 * но писать LR - еще хуже. Поэтому было принято решение левую рекурсию устранить.
 *
 * Итоговая грамматика:
 * program          ::= statement_list
 * statement_list   ::= statement statement_list | eps
 * statement        ::= assign_statement | if_statement | while_statement
 * assign_statement ::= variable '=' expression
 * if_statement     ::= 'if' expression statement_list 'end'
 * while_statement  ::= 'while' expression statement_list 'end'
 * expression       ::= compare_term (compare_operator compare_term)*
 * compare_term     ::= add_term (add_operator add_term)*
 * add_term         ::= factor_term (mult_operator factor_term)*
 * factor_term      ::= '(' expression ')'
 *                    | variable
 *                    | constant
 * compare_operator ::= '<' | '>'
 * add_operator     ::= '+' | '-'
 * mult_operator    ::= '*' | '/'
 */

class Parser(private val lexer: Lexer) {
    private lateinit var currentToken: Token

    // program ::= statement_list
    fun parseProgram(): StatementListNode {
        getNextToken()
        return parseStatementList()
    }


    // statement_list ::= statement statement_list | eps
    private fun parseStatementList(endMarker: TokenType = TokenType.EMPTY): StatementListNode {
        val astForest = StatementListNode(currentToken.line)

        while (currentToken.type != TokenType.ENDFILE && currentToken.type != endMarker) {
            val node = parseStatement()
            if (node != null)
                astForest.addNode(node)
        }

        return astForest
    }


    // statement ::= assign_statement | if_statement | while_statement
    private fun parseStatement(): Node? {
        return when (currentToken.type) {
            TokenType.IF                         -> parseIfStatement()
            TokenType.WHILE                      -> parseWhileStatement()
            TokenType.ID                         -> parseAssignStatement()
            TokenType.NEWLINE, TokenType.ENDFILE -> {
                getNextToken()
                return null
            }
            else                                 -> {
                panic("Unexpected token: ${currentToken.lexeme}")
                return null
            }
        }
    }


    // if_statement ::= 'if' expression statement_list 'end'
    private fun parseIfStatement(): Node {
        // Пропускаем 'if'
        getNextToken()

        val ifNode = IfNode(parseExpression(), currentToken.line)
        ifNode.addNodes(parseStatementList(TokenType.END).children)
        match(currentToken.type, TokenType.END, "Expected end keyword, but got ${currentToken.lexeme}")
        getNextToken()

        return ifNode
    }


    // while_statement ::= 'while' expression statement_list 'end'
    private fun parseWhileStatement(): Node {
        // Пропускаем 'while'
        getNextToken()

        val whileNode = WhileNode(parseExpression(), currentToken.line)
        whileNode.addNodes(parseStatementList(TokenType.END).children)
        match(currentToken.type, TokenType.END, "Expected end keyword, but got ${currentToken.lexeme}")
        getNextToken()

        return whileNode
    }


    // assign_statement ::= variable '=' expression
    private fun parseAssignStatement(): Node {
        val varNode = parseVariable()

        match(currentToken.type, TokenType.ASSIGN, "Expected '=', but got ${currentToken.lexeme}")
        getNextToken()

        return AssignNode(LinkedList(listOf(varNode, parseExpression())), currentToken.line)
    }


    // expression   ::= compare_term (('<' | '>') compare_term)*
    private fun parseExpression(): Node {
        val leftTerm = parseCompareTerm()
        var operatorNode: OperatorNode? = null

        while (currentToken.type != TokenType.NEWLINE && isCompareOperator(currentToken)) {
            val newCompareNode =
                if (currentToken.type == TokenType.LESS)
                    LessOperatorNode(currentToken.line)
                else
                    GreaterOperatorNode(currentToken.line)

            if (operatorNode == null) {
                operatorNode = newCompareNode
                operatorNode.addNode(leftTerm)
            }
            else {
                newCompareNode.addNode(operatorNode)
                operatorNode = newCompareNode
            }

            getNextToken()
            operatorNode.addNode(parseCompareTerm())
        }

        return operatorNode ?: leftTerm
    }


    // compare_term ::= add_term (('+' | '-') add_term)*
    private fun parseCompareTerm(): Node {
        val leftTerm = parseAddTerm()
        var operatorNode: OperatorNode? = null

        while (currentToken.type != TokenType.NEWLINE && isPlusOrMinusOperator(currentToken)) {
            val newAddNode =
                if (currentToken.type == TokenType.PLUS)
                    PlusOperatorNode(currentToken.line)
                else
                    MinusOperatorNode(currentToken.line)

            if (operatorNode == null) {
                operatorNode = newAddNode
                operatorNode.addNode(leftTerm)
            }
            else {
                newAddNode.addNode(operatorNode)
                operatorNode = newAddNode
            }

            getNextToken()
            operatorNode.addNode(parseAddTerm())
        }

        return operatorNode ?: leftTerm
    }


    // add_term ::= factor_term (('*' | '/') factor_term)*
    private fun parseAddTerm(): Node {
        val leftTerm = parseFactorTerm()
        var operatorNode: OperatorNode? = null

        while (currentToken.type != TokenType.NEWLINE && isMultOrDivOperator(currentToken)) {
            val newMultNode =
                if (currentToken.type == TokenType.MULT)
                    MultOperatorNode(currentToken.line)
                else
                    DivOperatorNode(currentToken.line)

            if (operatorNode == null) {
                operatorNode = newMultNode
                operatorNode.addNode(leftTerm)
            }
            else {
                newMultNode.addNode(operatorNode)
                operatorNode = newMultNode
            }

            getNextToken()
            operatorNode.addNode(parseFactorTerm())
        }

        return operatorNode ?: leftTerm
    }


    // factor_term ::= '(' expression ')' | variable | constant
    private fun parseFactorTerm(): Node {
        var factorTerm: Node? = null

        when (currentToken.type) {
            TokenType.LBRACKET -> {
                getNextToken()
                val expr = parseExpression()
                match(currentToken.type, TokenType.RBRACKET, "Expected ')', but got ${currentToken.lexeme}")
                getNextToken()
                factorTerm = expr
            }
            TokenType.ID -> factorTerm = parseVariable()
            TokenType.INTEGER -> factorTerm = parseConstant()
            else -> panic("Unexpected token: ${currentToken.lexeme}")
        }

        return factorTerm!!
    }

    private fun parseVariable(): Node {
        match(currentToken.type, TokenType.ID,"Expected id, but got ${currentToken.lexeme}")
        val varNode = VariableNode(currentToken.lexeme, currentToken.line)
        getNextToken()
        return varNode
    }

    private fun parseConstant(): Node {
        match(currentToken.type, TokenType.INTEGER, "Expected integer, but got ${currentToken.lexeme}")
        val constantNode = ConstantNode(currentToken.lexeme.toInt(), currentToken.line)
        getNextToken()
        return constantNode
    }


    private fun getNextToken() {
        currentToken = lexer.nextToken()
    }

    private fun <T> match(left: T, right: T, errorMsg: String) {
        if (left != right)
            panic(errorMsg)
    }

    // Под выкидывание исключения неслучайно был создан отдельный метод: здесь можно
    // в будущем реализовать восстановление после ошибки
    private fun panic(message: String) {
        throw ParserException(message, currentToken)
    }


    private fun isCompareOperator(token: Token): Boolean =
        token.type == TokenType.GREATER || token.type == TokenType.LESS

    private fun isPlusOrMinusOperator(token: Token): Boolean =
        token.type == TokenType.PLUS || token.type == TokenType.MINUS

    private fun isMultOrDivOperator(token: Token): Boolean =
        token.type == TokenType.MULT || token.type == TokenType.DIV

    private fun isOperator(token: Token): Boolean =
        isCompareOperator(token) || isPlusOrMinusOperator(token) || isMultOrDivOperator(token)
}