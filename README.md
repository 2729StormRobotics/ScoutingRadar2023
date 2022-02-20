# Scouting Radar 2022
Developed by Storm Robotics (FRC 2722 Charge and FRC 2720 Red Watch), Scouting Radar is an Android scouting app for FRC. To install, visit the Google Play Store. Feel free to fork and modify as well. Our stable branch is [main.](https://github.com/2729StormRobotics/ScoutingRadar2022/tree/main)

## Getting Started
Once you have installed the app, take a look at the Settings page. If you plan to use Bluetooth data transfer, the team number must match on all of your devices. We also recommend giving each device a unique device name, which will be displayed to the user as confirmation of data transfer.

## Configuration
The data that your team collects with Scouting Radar is completely configurable. There are three modes of scouting: Objective, Subjective, and Pit scouting. All three can be configured by creating a configuration file and uploading it in the app. See the [sample file](https://github.com/2729StormRobotics/ScoutingRadar2022/tree/main) for creation instructions. To upload it into the app, select "Configure Scouting" from the options menu. 

**Please note that it is your responsibility to ensure each of your devices is using the same configuration; Scouting Radar does not contain any schema validation at this time. While it is designed to simply ignore differences in imported data from different devices, it may fail unexpectedly.**

![Home Screen Options Menu](https://github.com/2729StormRobotics/ScoutingRadar2022/blob/integrationJosh/Screenshots/Home_Options.png?raw=true | width=400)

## Data Transfer
Scouting Radar is designed to be run on many devices at once. In order to centralize data, the app is equipped with importing and exporting capabilities. You can transfer data between instances of the app using both Bluetooth and QR codes. We recommend choosing one scout to be the "collector" that imports all of the data from the rest of the scouts. This collector can then export all of the data on its device into a CSV file, which can be analyzed using a program such as Excel or Tableau. See the Analysis section below for more details. 
## Objective Scouting
Objective scouting is the most "traditional" kind of scouting. It collects quantitative data on how robots score during a match. In Scouting Radar, objective data is *time-based*. The team configures a list of data points (called *actions*) to be collected, and they are presented as buttons to the scout. Once the scout presses the Start button, a stopwatch begins. When each action is pressed, the app records the timestamp of that action. The running history of actions is also displayed using their user-defined abbreviations, and the Undo button can be used to correct mistakes.

Objective scouting also includes spinners/drop-downs, intended for data such as endgame position.

## Subjective Scouting
Subjective scouting (inspired by 1678) is another form of scouting that is also based on a single robot in a single match, but is designed for the scout to rate the team's ability and performance, well, subjectively. Storm Robotics uses this feature to have scouts rate teams' driving skill and defensive abilities. 

Subjective scouting includes only spinners, with no time-based buttons.

## Pit Scouting
Pit scouting is the third form of scouting offered by Scouting Radar. This form of scouting is not per-match, and only per-team. It includes spinners for data collection (e.g. drivetrain type) and also a Notes field. 

> Written with [StackEdit](https://stackedit.io/).
