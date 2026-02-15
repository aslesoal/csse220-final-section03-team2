package model;

public class GameStateManager {

    private GameMode mode = GameMode.TITLE;

    private float titleAlpha = 1f;
    private float pauseAlpha = 0f;
    private float winAlpha = 0f;
    private float gameOverAlpha = 0f;

    private boolean newHighScore = false;
    private float highScoreAlpha = 0f;

    public GameMode getMode() { return mode; }
    public void setMode(GameMode mode) { this.mode = mode; }

    public boolean isTitle()    { return mode == GameMode.TITLE; }
    public boolean isPlaying()  { return mode == GameMode.PLAYING; }
    public boolean isPaused()   { return mode == GameMode.PAUSED; }
    public boolean isWin()      { return mode == GameMode.WIN; }
    public boolean isGameOver() { return mode == GameMode.GAME_OVER; }

    public float getTitleAlpha()    { return titleAlpha; }
    public float getPauseAlpha()    { return pauseAlpha; }
    public float getWinAlpha()      { return winAlpha; }
    public float getGameOverAlpha() { return gameOverAlpha; }

    public boolean isNewHighScore() { return newHighScore; }
    public float getHighScoreAlpha() { return highScoreAlpha; }

    public void setNewHighScore(boolean value) {
        newHighScore = value;
        highScoreAlpha = 0f;
    }

    public void updateFades() {

        if (!isTitle() && titleAlpha > 0f) {
            titleAlpha -= 0.02f;
            if (titleAlpha < 0f) titleAlpha = 0f;
        }

        if (isPaused()) {
            if (pauseAlpha < 1f) {
                pauseAlpha += 0.05f;
                if (pauseAlpha > 1f) pauseAlpha = 1f;
            }
        } else {
            pauseAlpha = 0f;
        }

        if (isWin()) {
            if (winAlpha < 1f) {
                winAlpha += 0.01f;
                if (winAlpha > 1f) winAlpha = 1f;
            }
        } else {
            winAlpha = 0f;
        }

        if (isGameOver()) {
            if (gameOverAlpha < 1f) {
                gameOverAlpha += 0.01f;
                if (gameOverAlpha > 1f) gameOverAlpha = 1f;
            }
        } else {
            gameOverAlpha = 0f;
        }

        if (newHighScore) {
            if (highScoreAlpha < 1f) {
                highScoreAlpha += 0.02f;
                if (highScoreAlpha > 1f) highScoreAlpha = 1f;
            }
        }
    }

    public void reset() {
        mode = GameMode.TITLE;
        titleAlpha = 1f;
        pauseAlpha = 0f;
        winAlpha = 0f;
        gameOverAlpha = 0f;

        newHighScore = false;
        highScoreAlpha = 0f;
    }
}
