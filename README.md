# Keyboard Input Viewer

KeyboardInputViewer is a program that displays keyboard input on your screen. It also shows what the average, maximum and current number of keys pressed per second is and images on 'images' forder. The program can also show a nice graph of the key presses over time.    
Almost all aspects of the program are also fully customizable. This program modified KeysPerSecond : [Original](https://github.com/RoanH/KeysPerSecond)


[Jump directly to downloads](#downloads)

# Introduction
Originally I wanted to make a keyboard input viewer like KeysPerSecond. So I modified RoanH's KeysPerSecond.

The program when active looks like this:    
![Interface](https://user-images.githubusercontent.com/53378637/128698767-a8a2ff04-463b-4c6a-8d38-fcc47f95224f.jpg)
![Interface](http://i.imgur.com/9cCzB0Q.png)  ![Interface](http://i.imgur.com/bLQXABw.png)    
There is also a right click menu to configure all the settings:    
![Menu](https://i.imgur.com/lltS2NK.png)    

For each configured key the program will show how many times it was pressed. And also you can show hero's image where it corresponding to. By default it will also show the maximum, average and current number of keys pressed per second.
When enabled it can also show a graph of the number of keys pressed per second over time and the total number of keys pressed.

Everything shown in the pictures above can be toggled on or off and all the panels can be arranged in a lot of different ways.      
![Config](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FbFCxJy%2FbtrczJtJByw%2Frei1RmyKHX9EiJiUXq6eCk%2Fimg.jpg)  
![Key config](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2Fbh9dlE%2FbtrczIBDh6S%2FW4iaKv84oXv52xKndNT1C0%2Fimg.jpg)  
![Key config1](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FcCGU7f%2FbtrczQsxoX8%2FURvne3EYQJO3AW1XClP021%2Fimg.jpg) 
![Layout](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2F0OZ3L%2FbtrczKGakrT%2FwlteKDGxhruXjXPelozD81%2Fimg.jpg)   
![Advanced layout settings](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FUo69f%2FbtrczIImQvv%2FvHyfLA5DMyRmdmWRI8cy2K%2Fimg.jpg)
![Bind image to key](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FbmvvVI%2FbtrcEv85HEy%2FLGuyXZOf9xcbP9kWH4OuE0%2Fimg.jpg)
![image resource folder](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FJTs3c%2FbtrcFWyCLhe%2FKJ4ucFf70umfjdDknEn3U0%2Fimg.jpg)

There are also some commands that can be sent to the program:    
**Ctrl + P**: Causes the program to reset the average and maximum value.    
**Ctrl + U**: Terminates the program.    
**Ctrl + I**: Causes the program to reset the key press statistics.    
**Ctrl + Y**: Shows / hides the GUI.    
**Ctrl + T**: Pauses / resumes the counter.    
**Ctrl + R**: Reloads the configuration file.

You can also move the program using the arrow keys or snap it to the edges of your screen.

Well I hope some of you find this program useful and/or will use it for your streams (I would love to see that happen  :) ).
And if you find any bugs feel free to report them. If you have any features you'd like to see added please tell me as well!

## Notes
- The horizontal line in the graph represents the average number of keys pressed per second.
- You can add any key, and any number of keys to the program.
- You can also add hero's image on mapping key.
- You can also track mouse buttons with this program.
- The overlay option is far form perfect it just ask the OS to place the program on top. It'll not overlay most full screen games.
- To change a GUI colour in the colours menu, click on the current colour
- An opacity of 100% means completely opaque and an opacity of 0% means completely transparent.
- The snap to screen edge function works on multi-monitor setups.
- You can move the window with the arrow keys at 3 different speeds 1, 2 & 3 pixels at a time (2=Ctrl, 3=Shift).
- You can pass the path to the config file to load via the command line or a shortcut so you can skip the configuration step. Setting the program as the default program to open the configuration file with may work as well as long as you don't move the executable afterwards.
- When resetting something it will also be printed to the console if this program is running using cmd/shell.    
- [For some people](https://youtu.be/E_WHAaI_-Zw) running the program in Windows 98 / ME compatibility mode makes it so it can overlay full screen osu! so if you're lucky this might work.

## Todo list / working on
It's kinda empty here right now :c, so please suggest things c:    

## Downloads
- [Zip](https://github.com/SsalHub/KeyboardInputViewer/releases/download/1.0/KeyboardInputViewer_1.0.zip)    

All releases: [releases](https://github.com/SsalHub/KeyboardInputViewer/releases)    
GitHub repository: [here](https://github.com/SsalHub/KeyboardInputViewer)

## Examples
The following two examples show the layout while it is being edited. All the panels have to line up with the grid, but the size of the grid cells can be changed.    
![](https://i.imgur.com/kfXaqwX.png)    
![](https://i.imgur.com/DP5MNVq.png)    
Next are two other examples of possible layouts.    
![](https://i.imgur.com/ImE4zTU.png)    
![](https://i.imgur.com/fBgohIA.png)    
Last are some very simple layouts to highlight the title-value display options.    
![Modes](https://i.imgur.com/0769n9E.png)      

## History
Project development started: 27 July 2021
