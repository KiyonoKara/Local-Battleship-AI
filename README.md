# Local Battleship AI
<div>
   <p>
      <a><img src="https://img.shields.io/badge/Language-Java-blue" alt="Java"></a>
      <a><img src="https://shields.io/github/languages/code-size/KiyonoKara/Local-Battleship-AI?color=ffc300" alt="Code Size"/></a>
   </p>
</div>

A project that lets you run a local server and let's play your battleship AI against the server. You can also play against other AI players without the server. All game sessions are printed and visualized on the console.

## Usage

### [Mode 1] Manual vs. AI  
User has two instances of `AbstractPlayer` play against each other. Automatic players are also allowed.  

**Directions:**
1. Create two separate instances of `AbstractPlayer` in the `Driver` class. 
2. Insert those two instances in the controller's parameters.
    ```java
    BattleSalvoController controller 
        = new BattleSalvoController(
            your_first_instance_here,
            your_second_instance_here
        );
    ```
3. Ensure the `.runGame()` method of the controller is called.
4. Your game is ready, follow the GUI's directions, and you're all set.

### [Mode 2] AI vs. Server  
User starts a local server and plays against it with an instance of `AbstractPlayer`.

**Directions:**
1. In the `Driver` class, make sure you have a valid AI player, an instance of `AbstractPlayer` that can play automatically.
2. Pass your player instance into the `ProxyController` instance to register your player.
3. Using the `Server.jar` file, run it with arguments `--debug all`.
4. Once the server is running, run your `Driver` with arguments that are the address and port.
   - Address: 0.0.0.0
   - 35001
5. Once your Driver is connected, you can watch as the session takes place.

## Tools
1. Build Automation: Gradle
2. Unit Testing: JUnit 5.8.1
3. Test Coverage: JaCoCo
4. JSON: FasterXML Jackson

## License
[MIT License](LICENSE.md)