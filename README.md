# Scrap Mechanic Mapper

Tool to create 2D maps for Scrap Mechanic Survival.

If you played a bit you probably got lost every now and then, and eventually noticed, that you find back home anyway. You gonna drive loooong rides to notice oh this was some until you see same places and even detect shortcuts. 
You may notice that everything is/feels grid like and that there are repetitive tiles. You may wonder how your world actually looks like...

But you now want a to see the big image - use this tool!

This tool draws lines for you, wherever your player walks, there is a preset of colors, which you can use for different types of things in the world.
But is this cheating or just enhanced locational awareness? The tool does not do the work for you, every line must be 'walked/driven in' by you.

You need CheatEngine to get the player position's memory addresses, see instructions below.

* Good with multi-monitors.
* Good with an in game built scooter. For exploration.

#### Why CheatEngine and not some Survival-Mod?

The game API does afaik not allow to export the position to a file. But this tool needs a constant update of it. So the only way of accessing it, is via some Windows API. Thus you need CheatEngine to figure out which address in memory the position is stored in.

You may however use a mod to print/display your position and greatly improve your search with CheatEngine. Just keep in mind, that when using multiplayer, all players must install that mod.

---

This is how the actual tool looks like: (You can see my current world I am playing on)
![This is how it looks like.](https://user-images.githubusercontent.com/18501527/84356158-17d50e80-abc4-11ea-94ac-dc421384016a.png)

[Big SPOILER version of image.](https://user-images.githubusercontent.com/18501527/84353825-1efa1d80-abc0-11ea-9ef3-3e41c34c1c75.png)

### Usage:

Pre-requirements: You have to install `Java Runtime Environment` and `CheatEngine`. CheatEngine is required to know where your player-position is stored in memory.

Install: Grab the latest [release](https://github.com/Ecconia/ScrapMechanicMapper/releases) (if any), or compile it yourself using e.g. IntelliJ or Maven (`mvn package`).

Instruction Video on [Youtube](https://youtu.be/yxnz1p_RWrk)

Launch: If your Java is installed properly, you can launch the tool by double-clicking it. If you prefer to, you may start it via a terminal of your choice using `java -jar <filename>` though. (Terminals like e.g. `cmd`).

Maps: Currently the tool always saves the map as `save.txt`, in the folder you run the .jar from, in general it is the same folder which the tool is in. If you have multiple worlds/maps, you have to rename the savefile if you want to switch between them.

CheatEngine usage:
- Select ScrapMechanic as the current program (button top left).
- Start a new scan looking for `float` values.
- You may filter that new scan from `-100000` to `100000` the beginner area (negative quadrant) of the island can be filtered with `-100000` to `-10`. But beware, that its an island you are walking on and it has the center at `0, 0`.
- Walk with your player and scan for `changed values`. (Always walk diagonal, the world has a grid, place a block to see it).
- Stand still with your player or just look around and scan for `unchanged values`.
- Repeat the last two steps until you have roughly 120 or better under 100 memory addresses.
- You will see the same two values over and over again, pick the one where you have at least 10 occurrences. Double-click a pair to have it highlighted by CheatEngine.
- Copy the address into this tool, the higher address of a pair goes into the first filed (sry for this step). Please confirm somehow that a drawn slope matches your movement, else swap the two addresses.
- Press `Done` and your position is now live transmitted.
- Hint: Most of the addresses you found are only valid while your player is alive. If you die, only roughly 10 of these addresses stay valid, but these will stay valid no matter if you die.
In case you died, you should switch to these remaining persistent addresses. I recommend you to keep CheatEngine open, until you died once. Cause then you don't have to filter all addresses again, each time you die.

Tool usage:
- All fields and buttons have Tooltips (hover with mouse over them), use it.
- The tool has a canvas (center) and a toolbar.
- The canvas draws the map, you can zoom it and move it around. The `Center` button moves it back to player position.
- The `Addr` button switches to the address-toolbar, the `Done` button switches back to the normal-toolbar.
- Press the `+` and then on the map, to add a waypoint. It will ask you for a label, only `Ok/Enter` will place it.
- By default, it doesn't draw lines, you have to select a color for that, if you don't want it to draw press `Off`.

Map interactions:
- You can zoom in and out by scrolling.
- You can drag your mouse around to move where you are looking on the map.
- You can select waypoints.
  - Pressing the `escape` key will clear your selection.
  - Pressing either `delete` or `backspace` will remove the selected waypoint.
  - Double clicking on a waypoint (whether selected or not) will let you to edit the name.

### TODOs

Usability:
- DON'T LET APPLICATION CRASH IF SCRAP MECHANIC IS NOT RUNNING (on attempt of entering addresses, not on launch).
- Allow viewing, deleting and moving of all line-end-points also decoupling and merging them.
- Allow changing color of path/line.
- Allow selecting moving deleting renaming of waypoints.
- Add different icons to waypoints (in general add icons and not circles).
- Add another color for cliffs.
- May somehow add biome areas (honestly no clue of how to yet).
- Allow adding points and lines from/with the GUI.
- Add guided memory-scanner to not rely on cheat engine.
- Add world/file chooser, current default is `save.txt`.
- Add undo and redo history.

Technical:
- Create a path instead of many lines (in memory at least).
- Improve the file structure, something binary is quite possible.
- Add clipping and bounds in the render routine.

#### Help
##### Ecconia
For casual help and issues, or any discussions about this project, you may contact me in the ScrapMechanic discord server, or on my [Development Discord Server](https://discord.com/invite/dYYxNvp). My username is always Ecconia.
##### RobotMan2412
I'm helping with the project, you can contact me as RobotMan2412#1572 on discord.