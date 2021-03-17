#ifndef DISPLAY_MANAGER_H
#define DISPLAY_MANAGER_H

#include <algorithm>
#include <cctype>
#include <iostream>
#include <SFML/Graphics.hpp>
#include <vector>

#include "Boardgame.h"

#define SF_PASTELBROWN_COLOR sf::Color(131, 105, 83)
#define WHITE_COLOR_PIECES sf::Color::White
#define BLACK_COLOR_PIECES sf::Color(75, 75, 75)

/* Dimension of the background image, for the screen to perfectly
  fit it */
#define WIN_WIDTH 1000
#define WIN_HEIGHT 715

class DisplayManager
{
  private:

    sf::Event m_Event;
    sf::RenderWindow m_Window;

    Boardgame m_Boardgame;
    bool m_CorrectBoard;

    static std::vector<char> pieces_symbols;

  public:

    /* Constructors */
    DisplayManager();

    /* Methods */
    void catchEvents();
    void clear();
    void display(std::vector<std::vector<char>>& board,
      sf::Texture& background, std::vector<sf::Texture>& pieces);
    bool isOpen();
};

#endif
