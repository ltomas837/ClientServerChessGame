SRC_DIR := src
H_DIR := include
SFML_DIR := third-party-libraries
BIN_DIR := bin
SRC_FILES := $(wildcard $(SRC_DIR)/*.cpp)
SFML_LIBRARIES := $(wildcard $(SFML_DIR)/*.so)
CPPFLAGS := -fPIC -I./$(H_DIR) -shared -o

all: $(SHARED_LIBRARY)
	g++ $(CPPFLAGS) ../../bin/classes/java/main/libgui.so $(SRC_FILES) $(SFML_LIBRARIES)

clean:
	rm $(wildcard $(BIN_DIR)/*)
