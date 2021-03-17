#ifndef BOARDGAME_H
#define BOARDGAME_H

#include <iostream>
#include <SFML/Graphics.hpp>
#include <vector>

#define SF_DARKBROWN_COLOR sf::Color(78, 53, 36)
#define SF_BLACK_COLOR sf::Color(0, 0, 0)
#define SF_GOLD_COLOR sf::Color(225, 184, 148)

#define BIGSQUARE_SIZE 600.0f
#define MEDIUMSQUARE_SIZE 550.0f
#define SMALLSQUARE_SIZE 500.0f
#define BOARDGAME_SIZE 480.0f
#define CELL_SIZE BOARDGAME_SIZE / 8.0f

#define SQUARES_NB 4
#define BLACKCELLS_PER_COL 4
#define BOARDGAME_ROWS 8
#define BOARDGAME_COLS 8
#define RECTANGLES_NB SQUARES_NB + BLACKCELLS_PER_COL * BOARDGAME_ROWS
#define TEXTS_NB BOARDGAME_ROWS + BOARDGAME_COLS

#define FONT_SIZE 20
#define FONT_WIDTH FONT_SIZE / 2

class Boardgame
{
  private:

    size_t m_Counter;
    bool m_Drawn;
    sf::Font m_Font;

    std::vector<sf::RectangleShape> m_Rectangles;
    std::vector<sf::Text> m_Texts;

    unsigned int m_X;
    unsigned int m_Y;

  public:

    /* Constructors */
    Boardgame(unsigned int win_width, unsigned int win_height);

    /* Methods */
    bool isDrawn();
    sf::Drawable& getUndrawnDrawable();
    void notDrawn();
    unsigned int getX(size_t row);
    unsigned int getY(size_t col);
};

#endif
