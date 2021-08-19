# Keys Per Second for LostSaga

KeysPerSecond for Lostsaga is a program that counts how many times certain keys are pressed. And shows what the average, maximum and current number of keys pressed per second is and image of LostSaga heroes. The program can also show a nice graph of the key presses over time.    
Almost all aspects of the program are also fully customizable. This program modified KeysPerSecond : [Original](https://github.com/RoanH/KeysPerSecond)


[Jump directly to downloads](#downloads)

# Introduction
Originally I wanted to make a keyboard input viewer like KeysPerSecond for LostSaga. So I modified RoanH's KPS.

The program when active looks like this:    
![Interface](https://user-images.githubusercontent.com/53378637/128698767-a8a2ff04-463b-4c6a-8d38-fcc47f95224f.jpg)
![Interface](http://i.imgur.com/9cCzB0Q.png)  ![Interface](http://i.imgur.com/bLQXABw.png)    
There is also a right click menu to configure all the settings:    
![Menu](https://i.imgur.com/lltS2NK.png)    

For each configured key the program will show how many times it was pressed. And also you can show hero's image where it corresponding to. By default it will also show the maximum, average and current number of keys pressed per second.
When enabled it can also show a graph of the number of keys pressed per second over time and the total number of keys pressed.

Everything shown in the pictures above can be toggled on or off and all the panels can be arranged in a lot of different ways.      
![Config](https://user-images.githubusercontent.com/53378637/128697306-06a41740-5e5c-413b-a3f6-fcb234ed42c2.jpg)  
![Key config](https://user-images.githubusercontent.com/53378637/128698093-ea198e06-9d77-4096-b7a6-4ffc128b37f3.jpg)   
![Layout](https://user-images.githubusercontent.com/53378637/128697309-30c1d2ba-bdd8-49ad-a7fd-9fa83677038f.jpg)   
![Advanced layout settings](https://user-images.githubusercontent.com/53378637/128697313-2234c050-ab65-43ee-8eaf-ac29113af6ca.jpg)
![Bind image to key](https://user-images.githubusercontent.com/53378637/128697312-8c55b0d4-7467-4478-b34b-3fd474983550.jpg)

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

## Pre-Created Config Files
![Pre-Created Config Files](https://user-images.githubusercontent.com/53378637/128699216-be6da37b-0467-41e4-b81b-a28045cecc35.jpg)
You don't have to set key/layout config from scratch. I already made some example config files on 'config' folder. You can load a file which you want to use.

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
- [Zip](https://github.com/SsalHub/LSkeyspersecond/releases/download/1.0/KeysPerSecond_LS_alpha-1.0.zip)    

All releases: [releases](https://github.com/SsalHub/LSkeyspersecond/releases)    
GitHub repository: [here](https://github.com/SsalHub/LSkeyspersecond)

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
