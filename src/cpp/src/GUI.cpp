#include "jni.h"
#include <vector>
#include <cstdio>

#include "GUI.h"
#include "DisplayManager.h"


/**
 * JNI function to display the GUI.
 */
JNIEXPORT void JNICALL Java_GUI_launchGUI(JNIEnv *env, jobject obj, jobjectArray jboard) {
  
  /* Get the board state from the client */
  std::vector<std::vector<char>> board;

	jint nb_rows = env->GetArrayLength(jboard);

  for (int i=0; i<nb_rows; i++) {
    jcharArray jrow = (jcharArray) (env->GetObjectArrayElement(jboard, i));
    jchar* jrowPtr = env->GetCharArrayElements(jrow, (jboolean *) 0);
    jint nb_columns = env->GetArrayLength(jrow);

    std::vector<char> row;
    for (int j=0; j<nb_columns; j++)
      row.push_back((char) jrowPtr[j]);

    board.push_back(row);

    env->ReleaseCharArrayElements(jrow, jrowPtr, 0);
    env->DeleteLocalRef(jrow);
  }

  /* Loading the textures */
  DisplayManager window;

  sf::Texture t;
  if (!t.loadFromFile("./assets/textures/background.png")){
      std::cerr << "couldn't load background texture\n";
      exit(1);
  }

  std::vector<sf::Texture> pieces;
  pieces.reserve(6);
  sf::Texture texture;

  for (const std::string path : {"./assets/textures/C.png",
    "./assets/textures/D.png", "./assets/textures/F.png",
    "./assets/textures/P.png", "./assets/textures/R.png",
    "./assets/textures/T.png"}){
    if (!texture.loadFromFile(path)){
      std::cerr << "couldn't load background texture\n";
      exit(1);
    } 
    else 
      pieces.emplace_back(texture);
  }

  /* Displaying loop */
  while(window.isOpen()) {
    window.catchEvents();
    window.clear();
    window.display(board, t, pieces);
  }


}
