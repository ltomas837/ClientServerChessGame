#include "DisplayManager.h"

std::vector<char> DisplayManager::pieces_symbols =
  {'c', 'C', 'd', 'D', 'f', 'F', 'p', 'P', 'r', 'R', 't', 'T'};

/**
 * Initializing the window
 */
DisplayManager::DisplayManager() :
  m_Boardgame(WIN_WIDTH, WIN_HEIGHT), m_CorrectBoard(true){

  m_Window.create(sf::VideoMode(WIN_WIDTH, WIN_HEIGHT), "Chess Game");
  m_Window.setPosition(sf::Vector2i(
    sf::VideoMode::getDesktopMode().width * 0.5 - WIN_WIDTH * 0.5,
    sf::VideoMode::getDesktopMode().height * 0.5 - WIN_HEIGHT * 0.5));

}

/**
 * Catch the event on the window
 */
void DisplayManager::catchEvents(){

  while(m_Window.pollEvent(m_Event)){

    /* Closing */
    if ((m_Event.type == sf::Event::EventType::Closed) ||
      (m_Event.key.code == sf::Keyboard::Escape))
      m_Window.close();
    /* Resizing */
    else if (m_Event.type == sf::Event::Resized){
      sf::View view = m_Window.getDefaultView();
      view.setSize(sf::Vector2f(m_Event.size.width, m_Event.size.height));
      m_Window.setView(view);
    }
  }
}

/**
 * Checks if the window is open. Closes the window if the server sends a wrong format.
 */ 
bool DisplayManager::isOpen(){
  return m_Window.isOpen() && m_CorrectBoard;
}

/**
 * Sets the background as brown.
 */ 
void DisplayManager::clear(){
  m_Window.clear(SF_PASTELBROWN_COLOR);
}

/**
 * Displays the content of the window and draw the content.
 */
void DisplayManager::display(std::vector<std::vector<char>>& board,
  sf::Texture& background, std::vector<sf::Texture>& pieces){
  m_CorrectBoard = board.size() == BOARDGAME_ROWS;

  for (std::vector<char> row : board){
    m_CorrectBoard &= row.size() == BOARDGAME_COLS;
  }

  if (m_CorrectBoard){
    /* Drawing the background */
    sf::Sprite sprite;
    sprite.setTexture(background);
    m_Window.draw(sprite);
    m_Boardgame.notDrawn();

    /* Drawing the board */
    while (!m_Boardgame.isDrawn())
      m_Window.draw(m_Boardgame.getUndrawnDrawable());

    char cell_content;

    /* Drawing the pieces */
    for (size_t row = 0; row < board.size(); ++row){
      for (size_t col = 0; col < board.at(row).size(); ++col){
        cell_content = board.at(row).at(col);

        /* Setting the color of the piece */
        if (std::isupper(cell_content))
          sprite.setColor(WHITE_COLOR_PIECES);
        else if (std::islower(cell_content))
          sprite.setColor(BLACK_COLOR_PIECES);
        

        std::vector<char>::iterator find_res = std::find(pieces_symbols.begin(),
          pieces_symbols.end(), cell_content);

        /* Drawing the piece */
        if (find_res != pieces_symbols.end()){
          sprite.setTexture(
            pieces.at((find_res - pieces_symbols.begin()) / 2));
          sprite.setPosition(m_Boardgame.getX(col), m_Boardgame.getY(row));
          m_Window.draw(sprite);
        } 
        else {
          if (cell_content != ' ')
            std::cerr << "Unrecognized char on the board: " << cell_content <<
              ".\nThis char is interpreted as an empty cell on the board.\n";
        }
      }
    }

    /* Display the window */
    m_Window.display();
  } 
  else {
    std::cerr << "8x8 board not recognized.\n";
    exit(1);
  }
}
