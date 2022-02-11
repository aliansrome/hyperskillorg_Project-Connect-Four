const val ROW_POS = 0
const val COLUMN_POS = 1
fun main() {
        println("Connect Four")
        println("First player's name:")
        val playerFirstName = readLine()!!
        println("Second player's name:")
        val playerSecondName = readLine()!!

        var inboardparam: String
        val reg = Regex("(\\s?)+(\\d)+(\\s?)+(X|x)(\\s?)+(\\d)+(\\s?)+")
        var conTrueParam: Boolean
        val boardParam = mutableListOf<Int>()
        do {
            println("""Set the board dimensions (Rows x Columns)
Press Enter for default (6 x 7)""")
            boardParam.clear()
            do {
                inboardparam = readLine()!!
                conTrueParam = if (inboardparam.matches(reg) || inboardparam.isEmpty()) {
                    true
                } else {
                    println("Invalid input")
            //raw text
                    println("""Set the board dimensions (Rows x Columns)
Press Enter for default (6 x 7)""")
            //end raw text
                    false
                }
            } while (!conTrueParam)

            if (inboardparam.isEmpty()) {
                boardParam.addAll(listOf(6, 7))
            } else {
                boardParam += inboardparam.split(Regex("x|X")).map { it.trim().toInt() }.toMutableList()
            }
            conTrueParam =if(boardParam[0] in 5..9) {
                if (boardParam[1] in 5..9) {
                    true
                } else {
                    println("Board columns should be from 5 to 9")
                    false
                }
            } else {
                println("Board rows should be from 5 to 9")
                false
            }
        }while (!conTrueParam)
        // get count game
        //raw text
    val strGetCountGame = """Do you want to play single or multiple games?
For a single game, input 1 or press Enter
Input a number of games:"""
    //end raw text
    val regCountGame = Regex("[1-9]+")
    var countGame = 0
    while (true) {
        println(strGetCountGame)
        val strCountGame = readLine()!!
        if (strCountGame.matches(regCountGame)) {
            countGame = strCountGame.toInt()
            break
        } else if (strCountGame.isBlank()){
            countGame = 1
            break
        } else {
            println("Invalid input")
        }
    }
        //end get
        //raw text
        println("""$playerFirstName VS $playerSecondName
${boardParam[ROW_POS]} X ${boardParam[COLUMN_POS]} board""")
        //end raw text
    if (countGame == 1){
        println("Single game")
    } else {
        println("Total $countGame games")
    }
    val board=createBoard(boardParam[COLUMN_POS], boardParam[ROW_POS])
    gameMain(playerFirstName,playerSecondName,board,countGame)
}

fun createBoard(columnCount :Int, rowCount: Int) :MutableList<MutableList<String>>{
    return MutableList(columnCount){ MutableList(rowCount){" "} }
}

fun gameMain(playerFirstName: String, playerSecondName: String, newBoard: MutableList<MutableList<String>>, gameCount :Int = 1){
    val strAskFirstPlayerName = "$playerFirstName's turn:"
    val strAskSecondPlayerName = "$playerSecondName's turn:"
    val listChekSimbol = mutableListOf("o","*")
    val listAsk = mutableListOf(strAskFirstPlayerName,strAskSecondPlayerName)
    val strColumnOutOfRange = "The column number is out of range (1 - ${newBoard.size})"
    val strIncorrectInput = "Incorrect column number"
    val strColumnFullReplase = "<COLL>"
    val strColumnFull = "Column $strColumnFullReplase is full"
    val strEndGame = "Game over!"
    val strGameDraw ="It is a draw"
    val strWinFirstPlayer = "Player $playerFirstName won"
    val strWinSecondPlayer = "Player $playerSecondName won"
    var playerFirstScore = 0
    var playerSecondScore =0
    var gameEnd = false
    var gameCurrent = 1
    while (gameCurrent <= gameCount) {
        var gameQueue = 0
        var board :MutableList<MutableList<String>> = mutableListOf()
        for (i in newBoard){
            board.add(i.toMutableList())
        }
        if (gameCount > 1){
            println("Game #$gameCurrent")
            gameQueue = if (gameCurrent % 2 != 0) 0 else 1
        }
        println(drawBoard(board))
        while (!gameEnd) {
            println(listAsk[gameQueue])
            var answer = readLine()!!
            if (answer.lowercase() == "end") {
                gameEnd = true
            } else if (!answer.matches(Regex("[+-]?\\d+"))) {
                println(strIncorrectInput)
            } else if (answer.toInt() !in 1..board.size) {
                println(strColumnOutOfRange)
            } else {
                val column = answer.toInt() - 1
                val indexLastRow = board[column].lastIndex
                if (board[column][indexLastRow] != " ") {
                    println(strColumnFull.replace(strColumnFullReplase, answer))
                } else {
                    //draw point
                    for (cellsIndex in board[column].indices) {
                        if (board[column][cellsIndex] == " ") {
                            board[column][cellsIndex] = listChekSimbol[gameQueue] //draw point
                            break
                        }
                    }
                    //draw board
                    println(drawBoard(board))
                    //test win
                    when (testWin(board)) {
                        GAME_DRAW -> {
                            println(strGameDraw)
                            playerFirstScore++
                            playerSecondScore++
                            break
                        }
                        GAME_WIN -> {
                            println(if (gameQueue == 0) {
                                playerFirstScore+=2
                                strWinFirstPlayer
                            } else {
                                playerSecondScore+=2
                                strWinSecondPlayer
                            })
                            break
                        }
                    }
                    //switch player
                    gameQueue = if (gameQueue == 1) 0 else 1
                }
            }
        }
        if (gameCount > 1 && !gameEnd){
            println("Score")
            println("$playerFirstName: $playerFirstScore $playerSecondName: $playerSecondScore")
        }
        if (gameEnd || gameCurrent == gameCount) {
            println(strEndGame)
            break
        }
        gameCurrent++
    }
}
const val GAME_CONT = 0
const val GAME_DRAW = 1
const val GAME_WIN = 2

fun testWin(board: MutableList<MutableList<String>>) :Int{
    //test game win
    // colon
    for (colon in board){
        if (testLineWin(colon)) return GAME_WIN
    }
    //row
    for (i in board[0].indices){
        val row = mutableListOf<String>()
        for (colon in board.indices){
            row.add(board[colon][i])
        }
        if (testLineWin(row)) return  GAME_WIN
    }

    //diagonal A

    val boardDiag = mutableListOf<MutableList<String>>()
    val boardDiagA = mutableListOf<MutableList<String>>()
    val countDiag = board.size + board[0].lastIndex
    for (iColon in 1 .. countDiag){
        boardDiag.add(mutableListOf())
        boardDiagA.add(mutableListOf())
    }
    for (colon in board.indices){
        for (row in board[0].indices){
            boardDiag[colon + row].add(board[colon][row])
            boardDiagA[board.lastIndex - colon + row].add(board[colon][row])
        }
    }
    for (i in boardDiag){
        if (testLineWin(i)) return GAME_WIN
    }
    for (i in boardDiagA){
        if (testLineWin(i)) return GAME_WIN
    }
    //test game draw
    var topLineFull = true
    for (colon in board){
        if (colon.last() == " ") topLineFull = false
    }
    if (topLineFull) return GAME_DRAW
    //end test game draw
    return GAME_CONT
}

fun testLineWin (colon :MutableList<String>) :Boolean {
    var colonPointLineO = 0
    var colonPointLineX = 0
    for (cells in colon){
        when (cells){
            "o" -> {
                colonPointLineO++
                colonPointLineX = 0
            }
            "*" -> {
                colonPointLineO = 0
                colonPointLineX++
            }
            " " -> {
                colonPointLineO = 0
                colonPointLineX = 0
            }
        }
        if (colonPointLineO == 4 || colonPointLineX == 4) return true
    }
    return false
}

fun drawBoard(body:MutableList<MutableList<String>>):String {
    var boardStr = ""
    var rowStr = ""
    //header
    repeat(body.size) {boardStr +=" ${it+1}"}
    //body
    val countCells = body.size-1
    val countRow = body[0].size-1
    for (row in countRow downTo 0){
        rowStr=""
        for (cells in 0 .. countCells ){
          rowStr+="║${body[cells][row]}"
        }
        rowStr= "$rowStr║"
        boardStr="$boardStr\n$rowStr"
    }
    //footer
    rowStr=""
    repeat(body.size-1){rowStr="$rowStr═╩"}
    rowStr="╚$rowStr═╝"
    boardStr="$boardStr\n$rowStr"

    return boardStr
}