package com.example.sudoku;


import android.annotation.SuppressLint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.OptionalInt;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class Solver {
    int [][] board;
    int selected_row;
    int selected_column;
    Random rand = new Random(System.nanoTime());
    // stores rows and columns of empty boxes (?)
    ArrayList<ArrayList<Object>> emptyBoxIndex;
    Solver(){
        selected_column = -1;
        selected_row = -1;
        board = new int[9][9];
        for (int i = 0; i < 9; i++){
            Arrays.fill(board[i],0);
        }

        emptyBoxIndex = new ArrayList<>();
    }

    // checks if number on board is valid.
    public boolean isValid(int[] pos, int num){

        // use selected row/column as position array.

        //checks row
        for (int i = 0; i < 9; i++) {
            if (this.board[pos[0]][i] == num && pos[1] != i) {
                return false;
            }
        }

        //checks column
        for (int i = 0; i < 9; i++) {
            if (this.board[i][pos[1]] == num && pos[0] != i){
                return false;
            }
        }

        // gets x and y of 3x3 the box position is in.
        int box_x = (pos[1]/3) * 3;
        int box_y = (pos[0]/3) * 3;

        // checks 3x3 box of the grid.
        int [] coords = new int[2];
        for (int i = box_y; i < box_y + 3; i++) {
            for (int j = box_x; j < box_x + 3; j++) {
                coords[0] = i;
                coords[1] = j;
                if (this.board[i][j] == num  && !Arrays.equals(coords,pos)){
                    return false;
                }
            }
        }
        return true;
    }

    // sets the chosen number on the board
    public void setNumberPos(int num){
        if (this.selected_row != -1  && this.selected_column != -1){
            if(this.board[this.selected_row-1][this.selected_column-1] == num){
                this.board[this.selected_row-1][this.selected_column-1] = 0;
            }
            else{
                this.board[this.selected_row-1][this.selected_column-1] = num;
            }
        }
    }

    // finds an empty index on board.
    public int[] findEmpty(){
        int[] pos = new int[2];
        for (int i = 0; i < 9; i++){
            for (int j = 0; j < 9; j++){
                pos[0] = i;
                pos[1] = j;

                if (this.board[i][j] == 0){
                    return pos;
                }
            }
        }
        return null;
    }

    public void getEmptyBoxIndexes(){
        for (int row = 0; row < 9; row++){
            for (int col = 0; col < 9; col++){
                if (this.board[row][col] == 0){
                    this.emptyBoxIndex.add(new ArrayList<>());
                    this.emptyBoxIndex.get(this.emptyBoxIndex.size()-1).add(row);
                    this.emptyBoxIndex.get(this.emptyBoxIndex.size()-1).add(col);
                }
            }
        }
    }

    public void undo(){
        if (this.selected_row != -1  && this.selected_column != -1) {
            this.board[this.selected_row - 1][this.selected_column - 1] = 0;
        }
    }

    public boolean solve(SudokuBoard display){
        int[] pos = findEmpty();
        if (pos == null){
            return true;
        }
        else{
            // solves board.
            for (int i = 1; i < 10; i++){
                if (isValid(pos, i)){
                    this.board[pos[0]][pos[1]] = i;
                    display.invalidate();
                    if (solve(display)){
                        return true;
                    }
                }
                this.board[pos[0]][pos[1]] = 0;
            }
        }
        return false;
    }

    public boolean randomSolve(){
        int[] pos = findEmpty();
        if (pos == null){
            return true;
        }

        else{
            // solves board.
            for (int i = 1; i < 10; i++){
                if (isValid(pos, i)){
                    this.board[pos[0]][pos[1]] = i;
                    if (randomSolve()){
                        return true;
                    }
                }
                this.board[pos[0]][pos[1]] = 0;
            }
        }
        return false;
    }

    public void clearBoard(){
        for (int i = 0; i < 9; i++){
            for (int j = 0; j < 9; j++){
                this.board[i][j] = 0;
            }
        }
        this.emptyBoxIndex = new ArrayList<>();
    }

    // generates a random sudoku board.
    public void randomBoard(){
        clearBoard();
        fillMatrix();
        randomSolve();
        removeDigits();
    }

    private void insertRandomNum(int row, int col){

        int min = 1;
        int max = 10;
        int num = rand.nextInt(9) + 1;
        int [] pos = new int[2];
        pos[0] = row;
        pos[1] = col;
        while (!isValid(pos, num)){
            num = rand.nextInt(9) + 1;
        }
        this.board[row][col] = num;
    }

    public void fillMatrix(){
        for (int i = 0; i < 9; i++){
            for (int j = 0; j < 9; j++){
                if (i < 3 && j < 3){
                   insertRandomNum(i,j);
                }

                else if((i >= 3  && i < 6) && (j >= 3 && j < 6)){
                    insertRandomNum(i,j);
                }

                else if (i >= 6 && j >= 6){
                    insertRandomNum(i,j);
                }
            }
        }
    }
    // removes random numbers on board so that board can be solved.
    private void removeDigits(){
        int min = 36;
        int max = 46;
        int count = rand.nextInt((max - min) + 1) + min;// amount of numbers that will be removed.
        while (count != 0){
            int i = rand.nextInt(9);
            int j = rand.nextInt(9);
            if(board[i][j] != 0){
                board[i][j] = 0;
            }
            count -= 1;
        }
    }

    public int[][] getBoard(){
        return this.board;
    }
    public ArrayList<ArrayList<Object>> getEmptyBoxIndex(){
        return this.emptyBoxIndex;
    }

    public int getSelectedRow(){
        return selected_row;
    }

    public int getSelectedColumn(){
        return selected_column;
    }

    public void setSelectedRow(int row){
        selected_row = row;
    }
    public void setSelectedColumn(int col){
        selected_column = col;
    }
}
