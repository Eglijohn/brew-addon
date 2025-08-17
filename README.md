<<<<<<< HEAD
# Meteor Addon Template

A template to allow easy usage of the Meteor Addon API.

### How to use

#### Use GitHub Template (Recommended)

- Click the green `Use this template` button in the top right corner of this page.  
  This will create a new repository with this template and a clean history.

#### Clone Manually

- Alternatively, clone this repository using these commands for a clean history:
  ```bash
  git clone --depth 1 https://github.com/MeteorDevelopment/meteor-addon-template your-addon-name
  cd your-addon-name
  rm -rf .git
  git init
  git add .
  git commit -m "Initial commit from template"
  ```

#### Development

- Use this template to add custom modules, commands, HUDs, and other features to Meteor Client.
- To test, run the `Minecraft Client` configuration in your IDE.
  This will start a Minecraft client with the Meteor Client mod and your addon loaded.
- To build, run the gradle `build` task. This will create a JAR file in the `build/libs` folder.
    - Move the JAR file to the `mods` folder of your Minecraft installation, alongside the Meteor Client mod and run the
      game.

### Updating to newer Minecraft versions

To update this template to a newer Minecraft version, follow these steps:

1. Ensure a Meteor Client snapshot is available for the new Minecraft version.
2. Update `gradle.properties`:
    - Set `minecraft_version`, `yarn_mappings` and `loader_version` to the new version.
    - Update any additional dependencies accordingly.
3. Update Loom:
    - Change the `loom_version` in `build.gradle.kts` to the latest version compatible with the new Minecraft version.
4. Update the Gradle wrapper:
    - You can find the latest Gradle version [here](https://gradle.org/releases/).
    - Run the `./gradlew wrapper --gradle-version <version>; ./gradlew wrapper` command to update the wrapper script.
5. Update your source code:
    - Adjust for Minecraft or Yarn mapping changes: method names, imports, mixins, etc.
    - Check for Meteor Client API changes that may affect your addon by comparing against the
      [master branch](https://github.com/MeteorDevelopment/meteor-client/tree/master).
6. Build and test:
    - Run the gradle `build` task.
    - Confirm the build succeeds and your addon works with the new Minecraft version.

### Project structure

```text
.
│── .github
│   ╰── workflows
│       │── dev_build.yml
│       ╰── pull_request.yml
│── gradle
│   ╰── wrapper
│       │── gradle-wrapper.jar
│       ╰── gradle-wrapper.properties
│── src
│   ╰── main
│       │── java
│       │   ╰── com
│       │       ╰── example
│       │           ╰── addon
│       │               │── commands
│       │               │   ╰── CommandExample
│       │               │── hud
│       │               │   ╰── HudExample
│       │               │── modules
│       │               │   ╰── ModuleExample
│       │               ╰── AddonTemplate
│       ╰── resources
│           │── assets
│           │   ╰── template
│           │       ╰── icon.png
│           │── addon-template.mixins.json
│           ╰── fabric.mod.json
│── .editorconfig
│── .gitignore
│── build.gradle.kts
│── gradle.properties
│── gradlew
│── gradlew.bat
│── LICENSE
│── README.md
╰── settings.gradle.kts
```

This is the default project structure. Each folder/file has a specific purpose.  
Here is a brief explanation of the ones you might need to modify:

- `.github/workflows`: Contains the GitHub Actions configuration files.
- `gradle`: Contains the Gradle wrapper files.  
  Edit the `gradle.properties` file to change the version of the Gradle wrapper.
- `src/main/java/com/example/addon`: Contains the main class of the addon.  
  Here you can register your custom commands, modules, and HUDs.  
  Edit the `getPackage` method to reflect the package of your addon.
- `src/main/resources`: Contains the resources of the addon.
    - `assets`: Contains the assets of the addon.  
      You can add your own assets here, separated in subfolders.
        - `template`: Contains the assets of the template.  
          You can replace the `icon.png` file with your own addon icon.  
          Also, rename this folder to reflect the name of your addon.
    - `addon-template.mixins.json`: Contains the Mixin configuration for the addon.  
      You can add your own mixins in the `client` array.
    - `fabric.mod.json`: Contains the metadata of the addon.  
      Edit the various fields to reflect the metadata of your addon.
- `build.gradle.kts`: Contains the Gradle build script.  
  You can manage the dependencies of the addon here.  
  Remember to keep the `fabric-loom` version up-to-date.
- `gradle.properties.kts`: Contains the properties of the Gradle build.  
  These will be used by the build script.
- `LICENSE`: Contains the license of the addon.  
  You can edit this file to change the license of your addon.
- `README.md`: Contains the documentation of the addon.  
  You can edit this file to reflect the documentation of your addon, and showcase its features.

## License

This template is available under the CC0 license. Feel free to use it for your own projects.
=======
<p align="center">
    <img src="BrewAddonLogo.png" width="200" height="200" style="image-rendering: pixelated;">
</p>

<p align="center">
  <img src="https://img.shields.io/badge/status-WIP-yellow" />
  <img src="https://img.shields.io/badge/PRs-welcome-yellow" />
  <img src="https://img.shields.io/badge/made%20with-%F0%9F%8D%BA-ffe200" />
</p>

# 🍺 Brew Addon (WIP)
_A Meteor addon for the drunks_

## ⬇ Installation
1. Make sure you have [Meteor Client](https://meteorclient.com) installed.
2. Drop `brew-addon.jar` into your `.minecraft/mods` folder. 

## 🍻 Contributing
Got a new feature? PRs are welcome!

<details>
<summary>📋 To Do</summary>

- [ ] Add addon structure  
- [ ] Teleport Util  
- [ ] Add modules  
  - [ ] R3akeOn3_'s Auto Sign  
  - [ ] R3akeOn3_'s Shulker Preview  
  - [ ] Eglijohn's stuff (WIP)  
- [ ] Make support for 1.21.1 - Latest  
- [ ] Get people to actually use it  

</details>

---

Made with 🍺 by people who probably shouldn't be trusted with code.
>>>>>>> 70b262931aa34bd20cccdecae56fd6770fa2e48f
