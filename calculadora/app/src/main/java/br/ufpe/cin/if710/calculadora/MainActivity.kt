package br.ufpe.cin.if710.calculadora

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity() {
    private var keyEXPR = "currExpr"
    private var keyRESULT = "currResult"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        text_info.text = savedInstanceState?.getString(keyEXPR)
        text_calc.setText(savedInstanceState?.getString(keyRESULT))
        var hasResult = false //Sempre falso, exceto quando é logo depois do botão btn_Equal ser apertado
        //Listener para os numeros na tela
        //a condição é pra verificar se é pra reiniciar a expressão ou continua na mesma.
        btn_0.setOnClickListener{ if(hasResult) { text_calc.setText("0") ; hasResult = false} else text_calc.setText(text_calc.text.toString() + "0") }
        btn_1.setOnClickListener{ if(hasResult) { text_calc.setText("1") ; hasResult = false} else text_calc.setText(text_calc.text.toString() + "1")}
        btn_2.setOnClickListener{ if(hasResult) { text_calc.setText("2") ; hasResult = false} else text_calc.setText(text_calc.text.toString() + "2") }
        btn_3.setOnClickListener{ if(hasResult) { text_calc.setText("3") ; hasResult = false} else text_calc.setText(text_calc.text.toString() + "3")}
        btn_4.setOnClickListener{ if(hasResult) { text_calc.setText("4") ; hasResult = false} else text_calc.setText(text_calc.text.toString() + "4") }
        btn_5.setOnClickListener{ if(hasResult) { text_calc.setText("5") ; hasResult = false} else text_calc.setText(text_calc.text.toString() + "5")}
        btn_6.setOnClickListener{ if(hasResult) { text_calc.setText("6") ; hasResult = false} else text_calc.setText(text_calc.text.toString() + "6") }
        btn_7.setOnClickListener{ if(hasResult) { text_calc.setText("7") ; hasResult = false} else text_calc.setText(text_calc.text.toString() + "7") }
        btn_8.setOnClickListener{ if(hasResult) { text_calc.setText("8") ; hasResult = false} else text_calc.setText(text_calc.text.toString() + "8") }
        btn_9.setOnClickListener{ if(hasResult) { text_calc.setText("9") ; hasResult = false} else text_calc.setText(text_calc.text.toString() + "9") }

        //Listener para as operações na tela.
        //Não tem a condição de resultado pois, caso a pessoa digite uma operação, pode ser que ela deseje continuar o que tinha sido escrito antes
        // (comportamento similar ao da calculadora do android, porém ela só tem um campo e a expressão é substituída) pelo resultado
        btn_Divide.setOnClickListener  { text_calc.setText(text_calc.text.toString() + "/") ; hasResult = false }
        btn_Multiply.setOnClickListener{ text_calc.setText(text_calc.text.toString() + "*") ; hasResult = false }
        btn_Subtract.setOnClickListener{ text_calc.setText(text_calc.text.toString() + "-") ; hasResult = false }
        btn_Add.setOnClickListener     { text_calc.setText(text_calc.text.toString() + "+") ; hasResult = false }
        btn_Dot.setOnClickListener     { text_calc.setText(text_calc.text.toString() + ".") ; hasResult = false }
        btn_LParen.setOnClickListener  { text_calc.setText(text_calc.text.toString() + "(") ; hasResult = false }
        btn_RParen.setOnClickListener  { text_calc.setText(text_calc.text.toString() + ")") ; hasResult = false }
        btn_Power.setOnClickListener   { text_calc.setText(text_calc.text.toString() + "^") ; hasResult = false }
        btn_Clear.setOnClickListener   { text_info.text = "" ; text_calc.setText("") ; hasResult = false }
        btn_Equal.setOnClickListener{
            try {
                text_info.text = eval(text_calc.text.toString()).toString()
                hasResult = true
            } catch (e: Exception){
                //lançando um toast com a exceção capturada, apenas com a mensagem da mesma
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    //Como usar a função:
    // eval("2+2") == 4.0
    // eval("2+3*4") = 14.0
    // eval("(2+3)*4") = 20.0
    //Fonte: https://stackoverflow.com/a/26227947
    fun eval(str: String): Double {
        return object : Any() {
            var pos = -1
            var ch: Char = ' '
            fun nextChar() {
                val size = str.length
                ch = if ((++pos < size)) str.get(pos) else (-1).toChar()
            }

            fun eat(charToEat: Char): Boolean {
                while (ch == ' ') nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parse(): Double {
                nextChar()
                val x = parseExpression()
                if (pos < str.length) throw RuntimeException("Caractere inesperado: " + ch)
                return x
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)`
            // | number | functionName factor | factor `^` factor
            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    if (eat('+'))
                        x += parseTerm() // adição
                    else if (eat('-'))
                        x -= parseTerm() // subtração
                    else
                        return x
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    if (eat('*'))
                        x *= parseFactor() // multiplicação
                    else if (eat('/'))
                        x /= parseFactor() // divisão
                    else
                        return x
                }
            }

            fun parseFactor(): Double {
                if (eat('+')) return parseFactor() // + unário
                if (eat('-')) return -parseFactor() // - unário
                var x: Double
                val startPos = this.pos
                if (eat('(')) { // parênteses
                    x = parseExpression()
                    eat(')')
                } else if ((ch in '0'..'9') || ch == '.') { // números
                    while ((ch in '0'..'9') || ch == '.') nextChar()
                    x = java.lang.Double.parseDouble(str.substring(startPos, this.pos))
                } else if (ch in 'a'..'z') { // funções
                    while (ch in 'a'..'z') nextChar()
                    val func = str.substring(startPos, this.pos)
                    x = parseFactor()
                    if (func == "sqrt")
                        x = Math.sqrt(x)
                    else if (func == "sin")
                        x = Math.sin(Math.toRadians(x))
                    else if (func == "cos")
                        x = Math.cos(Math.toRadians(x))
                    else if (func == "tan")
                        x = Math.tan(Math.toRadians(x))
                    else
                        throw RuntimeException("Função desconhecida: " + func)
                } else {
                    throw RuntimeException("Caractere inesperado: " + ch.toChar())

                }
                if (eat('^')) x = Math.pow(x, parseFactor()) // potência
                return x
            }
        }.parse()
    }
    //salvando a expressão e o resultado pra sobreviverem à mudanças de configuracao
    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putString(keyEXPR,text_info.text.toString())
        outState?.putString(keyRESULT,text_calc.text.toString())
        super.onSaveInstanceState(outState)
    }
}
