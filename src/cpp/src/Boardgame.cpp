#include "Boardgame.h"

/**
 * Building the board. Fill the rectangles and fonts to draw in attributes.
 */
Boardgame::Boardgame(unsigned int win_width, unsigned int win_height) :
  m_Counter(0), m_Drawn(false), m_X((win_width - BOARDGAME_SIZE) / 2.0f),
  m_Y((win_height - BOARDGAME_SIZE) / 2.0f){

  /* Drawing the rectangles for the board (except cells) */
  m_Rectangles.reserve(RECTANGLES_NB);

  sf::RectangleShape square(sf::Vector2f(BIGSQUARE_SIZE, BIGSQUARE_SIZE));
  square.setFillColor(SF_DARKBROWN_COLOR);
  square.setOrigin(BIGSQUARE_SIZE / 2.0f, BIGSQUARE_SIZE / 2.0f);
  square.setPosition(win_width / 2, win_height / 2);
  square.setOutlineThickness(3);
  square.setOutlineColor(SF_BLACK_COLOR);
  m_Rectangles.emplace_back(square);

  square =
    sf::RectangleShape(sf::Vector2f(MEDIUMSQUARE_SIZE, MEDIUMSQUARE_SIZE));
  square.setFillColor(SF_GOLD_COLOR);
  square.setOrigin(MEDIUMSQUARE_SIZE / 2.0f, MEDIUMSQUARE_SIZE / 2.0f);
  square.setPosition(win_width / 2, win_height / 2);
  square.setOutlineThickness(3);
  square.setOutlineColor(SF_BLACK_COLOR);
  m_Rectangles.emplace_back(square);

  square =
    sf::RectangleShape(sf::Vector2f(SMALLSQUARE_SIZE, SMALLSQUARE_SIZE));
  square.setFillColor(SF_DARKBROWN_COLOR);
  square.setOrigin(SMALLSQUARE_SIZE / 2.0f, SMALLSQUARE_SIZE / 2.0f);
  square.setPosition(win_width / 2, win_height / 2);
  m_Rectangles.emplace_back(square);

  square = sf::RectangleShape(sf::Vector2f(BOARDGAME_SIZE, BOARDGAME_SIZE));
  square.setFillColor(SF_GOLD_COLOR);
  square.setOrigin(BOARDGAME_SIZE / 2.0f, BOARDGAME_SIZE / 2.0f);
  square.setPosition(win_width / 2, win_height / 2);
  m_Rectangles.emplace_back(square);

  square = sf::RectangleShape(sf::Vector2f(CELL_SIZE, CELL_SIZE));
  square.setFillColor(SF_DARKBROWN_COLOR);
  square.move(m_X, m_Y);

  /* Drawing the cells */
  for (unsigned int j = 0; j < BLACKCELLS_PER_COL; ++j)
  {
    for (unsigned int i = 0; i < BOARDGAME_ROWS; ++i)
    {
      if ((i / 2) * 2 == i) /* Checking i is even... % is time-consuming */
      {
        square.move(0.0f, CELL_SIZE);
      } else {
        square.move(0.0f, -CELL_SIZE);
      }

      m_Rectangles.emplace_back(square);
      square.move(CELL_SIZE, 0.0f);
    }

    square.move(-8.0f * CELL_SIZE, 0.0f);
    square.move(0.0f, CELL_SIZE * 2.0f);
  }

  /* Drawing the fonts */
  m_Texts.reserve(TEXTS_NB);

  if (!m_Font.loadFromFile("./assets/fonts/UbuntuMono-B.ttf"))
  {
    std::cerr << "couldn't load font\n";
  }

  sf::Text text;
  text.setFont(m_Font);
  text.setString('8');
  text.setCharacterSize(FONT_SIZE);

  /* https://stackoverflow.com/questions/14505571/centering-text-on-the-screen-with-sfml */
  text.setOrigin(text.getLocalBounds().left / 2.0f,
    text.getLocalBounds().top / 2.0f);

  text.setFillColor(SF_DARKBROWN_COLOR);
  text.setPosition((win_width - MEDIUMSQUARE_SIZE
    + ((MEDIUMSQUARE_SIZE - SMALLSQUARE_SIZE) / 2.0f) - FONT_WIDTH) / 2.0f,
    m_Y + (CELL_SIZE - FONT_SIZE) / 2.0f);
  m_Texts.emplace_back(text);

  for (char number : {'7', '6', '5', '4', '3', '2', '1'})
  {
    text.setString(number);
    text.move(0.0f, CELL_SIZE);
    m_Texts.emplace_back(text);
  }

  text.setPosition(m_X + (CELL_SIZE - FONT_WIDTH) / 2.0f,
    (win_height + SMALLSQUARE_SIZE) / 2.0f
    + (MEDIUMSQUARE_SIZE - SMALLSQUARE_SIZE) / 4.0f - FONT_SIZE / 2.0f);

  for (char letter : {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'})
  {
    text.setString(letter);
    m_Texts.emplace_back(text);
    text.move(CELL_SIZE, 0.0f);
  }
}

/**
 *  Checks if the board is fully drawn.
 */
bool Boardgame::isDrawn()
{
  return m_Drawn;
}

/**
  * Gets the drawable to draw.
  */
sf::Drawable& Boardgame::getUndrawnDrawable()
{
  /* Useful line for debug or verbose mode:
    std::cout << "count = " << m_Counter << ", rect_size = " << m_Rectangles.size() << ", text_size = " << m_Texts.size() << '\n';
  */
  if (m_Counter + 1 <= m_Rectangles.size())
  {
    ++m_Counter;
    return m_Rectangles.at(m_Counter - 1);
  } else if (m_Counter + 1 <= m_Rectangles.size() + m_Texts.size()) {
    ++m_Counter;
    return m_Texts.at(m_Counter - m_Rectangles.size() - 1);
  } else {
    m_Drawn = true;
    return m_Texts.at(m_Texts.size() - 1);
  }
}

/**
 * Reinitialization to draw the board again.
 */
void Boardgame::notDrawn()
{
  m_Counter = 0;
  m_Drawn = false;
}

/**
 * Gets the x to draw a piece.
 */
unsigned int Boardgame::getX(size_t row)
{
  return m_X + row * CELL_SIZE;
}

/**
 * Gets the y to draw the piece.
 */
unsigned int Boardgame::getY(size_t col)
{
  return m_Y + col * CELL_SIZE;
}
