package com.example.sudoku;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.content.res.TypedArray;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class SudokuBoard extends View {
    private final int boardColor; // color of the board.
    private final int cellsHighlightColor; // highlights sudoku cell chosen
    private final int letterColor; // color nums
    private final int solveColor; // color of nums when solving board
    private final int errorColor; // color when an num is invalid on board
    private final Paint boardColorPaint = new Paint();
    private final Paint cellsHighlightColorPaint = new Paint();
    private final Paint letterPaint = new Paint();
    private final Rect letterPaintBounds = new Rect();
    private final Paint errorColorPaint = new Paint();
    private int cellSize;
    private final Solver solver = new Solver();

    public SudokuBoard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SudokuBoard,
                0,0);
        try{
            boardColor = a.getInteger(R.styleable.SudokuBoard_boardColor,0);
            cellsHighlightColor = a.getInteger(R.styleable.SudokuBoard_cellsHighlightColor,0);
            letterColor = a.getInteger(R.styleable.SudokuBoard_letterColor,0);
            solveColor = a.getInteger(R.styleable.SudokuBoard_solveColor,0);
            errorColor = a.getInteger(R.styleable.SudokuBoard_errorColor,0);
        } finally {
            a.recycle();
        }

    }

    private void placeNumber(Canvas canvas, int row, int col){
        // gets the value user inputs and converts it into a string.
        String text;
        int[] pos = new int[2];
        pos[0] = row;
        pos[1] = col;
        int num = solver.getBoard()[row][col];
        if (!solver.isValid(pos,num)){
            text = Integer.toString(solver.getBoard()[row][col]*-1);
            // colors cell when an invalid placement is made.
            canvas.drawRect((col + 1) * cellSize,(row + 1) * cellSize, col * cellSize, row * cellSize,
                    errorColorPaint);
        }
        else{
            text = Integer.toString(solver.getBoard()[row][col]);
        }

        float width, height;

        // gets height of the text.
        letterPaint.getTextBounds(text, 0, text.length(), letterPaintBounds);
        width = letterPaint.measureText(text);
        height = letterPaintBounds.height();
        // places number on sudoku board.
        canvas.drawText(text, (col * cellSize) + ((cellSize - width) / 2),
                (row * cellSize + cellSize) - ((cellSize - height) / 2), letterPaint);
    }

    private void drawNumbers(Canvas canvas){

        letterPaint.setTextSize(cellSize);
        for (int row = 0; row < 9; row++){
            for (int col = 0; col < 9; col++){
                if (solver.getBoard()[row][col] != 0) {
                    letterPaint.setColor(letterColor);
                    placeNumber(canvas,row,col);
                }
            }
        }

        letterPaint.setColor(solveColor);

        // used for when solving.
        for (ArrayList<Object> letter : solver.getEmptyBoxIndex()){
            int row = (int) letter.get(0);
            int col = (int) letter.get(1);
            placeNumber(canvas,row,col);

        }
    }


    // adjusts sudoku board for dimensions of the user's device.
    @Override
    protected void onMeasure(int width, int height){
        super.onMeasure(width, height);

        int dimension = Math.min(this.getMeasuredWidth(),this.getMeasuredHeight());
        cellSize = dimension / 9;
        setMeasuredDimension(dimension,dimension);
    }

    @Override
    protected void onDraw(Canvas canvas){
        boardColorPaint.setStyle(Paint.Style.STROKE);
        boardColorPaint.setStrokeWidth(16);
        boardColorPaint.setColor(boardColor);
        boardColorPaint.setAntiAlias(true);

        cellsHighlightColorPaint.setStyle(Paint.Style.FILL);
        boardColorPaint.setAntiAlias(true);
        cellsHighlightColorPaint.setColor(cellsHighlightColor);

        errorColorPaint.setStyle(Paint.Style.FILL);
        errorColorPaint.setAntiAlias(true);
        errorColorPaint.setColor(errorColor);

        letterPaint.setStyle(Paint.Style.FILL);
        letterPaint.setAntiAlias(true);
        letterPaint.setColor(letterColor);
        colorCell(canvas, solver.getSelectedRow(), solver.getSelectedColumn());
        canvas.drawRect(0,0, getWidth(), getHeight(), boardColorPaint);
        drawBoard(canvas);
        drawNumbers(canvas);
    }

    private void drawThickLine(){
        boardColorPaint.setStyle(Paint.Style.STROKE);
        boardColorPaint.setStrokeWidth(10);
        boardColorPaint.setColor(boardColor);
    }

    private void drawThinLine(){
        boardColorPaint.setStyle(Paint.Style.STROKE);
        boardColorPaint.setStrokeWidth(6);
        boardColorPaint.setColor(boardColor);
    }

    private void drawBoard(Canvas canvas){
        for (int col = 0; col < 10; col++){
            if (col % 3 == 0){
                drawThickLine();
            }
            else{
                drawThinLine();
            }
            canvas.drawLine(cellSize * col, 0,
                    cellSize * col, getWidth(), boardColorPaint);
        }

        for (int row = 0; row < 10; row++){
            if (row % 3 == 0){
                drawThickLine();
            }
            else{
                drawThinLine();
            }
            canvas.drawLine(0, cellSize * row,
                    getWidth(), cellSize * row, boardColorPaint);
        }
    }

    public Solver getSolver(){
        return this.solver;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override // this gives us data about touch events that happen on the user's screen,
    // so if the user swipes the screen or if the user taps the screen we can get like the
    // x and y coordinates of the users tap (we'll be using this)
    public boolean onTouchEvent(MotionEvent event) {
        boolean btnTouched;
        float x = event.getX();
        float y = event.getY();

        // differentiates between types of taps that occur on screen.
        int action = event.getAction();

        if (action == MotionEvent.ACTION_DOWN){
            solver.setSelectedRow((int) Math.ceil(y/cellSize));
            solver.setSelectedColumn((int) Math.ceil(x/cellSize));
            btnTouched = true;
        }

        else{
            btnTouched = false;
        }
        return btnTouched;

    }

    private void colorCell(Canvas canvas, int row, int col){
        if(solver.getSelectedColumn() != -1 && solver.getSelectedRow() != -1){
            // colors cell.
            canvas.drawRect((col - 1)*cellSize,(row - 1) * cellSize, col * cellSize, row * cellSize,
                    cellsHighlightColorPaint);
        }

        invalidate();
    }
}
