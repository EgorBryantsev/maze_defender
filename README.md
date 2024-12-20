# Maze Defender

This project is a tower defense game developed in Java using Swing, where you defend a maze from enemies that try to navigate through it.

This project is a learning exercise in implementing two new topics: A* pathfinding for enemy movement and Git for version control.

https://github.com/EgorBryantsev/maze_defender

## Advanced topics
### A* Pathfinding
The goal was to implement this algorithm to allow enemies to move through a randomly generated maze. Enemies have a start and end point given, and calculate the optimal path using this.

We learned about this algorithm from online resources and programming tutorials, notably the A* Search Algorithm page by geeksforgeeks.org. This was our most important resource in learning how to implement this.

### Git
We used git for collaborative coding and version control to manage code changes and track progress. 

We learned how to use this from the GitHub "beginners guide to Git", and the Git Tutorial from w3schools.com.

Because of this, we were able to work on the project more efficiently and quickly.

## How to play
When you start the game a maze will be generated and enemies will begin to spawn. There are a number of 2x2 squares in the maze which can be clicked on to upgrade them, which will create towers which will shoot at the enemies (because of how the maze is generated, sometimes towers are far from the enemies' path. In this case, you can choose to build a sniper tower instead which is lower but has no range limit). Eliminating enemies grants money, which can be used to upgrade towers. Your goal is to survive as long as possible.

Towers must be clicked in the top left tile to upgrade.

## Sources
The A* Search Algorithm - https://www.geeksforgeeks.org/a-search-algorithm/
A* pathfinding - https://www.youtube.com/watch?v=-L-WgKMFuhE
Beginner's Guide to Git - https://github.blog/developer-skills/programming-languages-and-frameworks/what-is-git-our-beginners-guide-to-version-control/
Git Tutorial - https://www.w3schools.com/git/default.asp

Textures are taken from https://opengameart.org/art-search-advanced?keys=&field_art_type_tid%5B%5D=14&sort_by=count&sort_order=DESC
