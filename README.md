# CSE 535 Emergency Alarm

###### tags: `acoustics model`, `decision tree algorithm`, `microphone`, `Android API Level 22+`

> The objective of the project is to build a mobile application that can help deaf and hard of hearing individuals to respond to nearby emergency vehicles and avoid any potential hazards. The project is a perfect amalgamation of different concepts that we learnt as part of the course. Use of the best mobile computing model, building an android app, applying concepts of power and energy and using data analysis models to identify a specific sound, are covered as part of the project development. We also take care of the way the idea would be presented to the user, to ensure maximum usability of the app and at the same time make it work in the context of pedestrians, drivers and other sounds on a busy road. Provision of having multiple ways to be notified - flash screens or user controlled vibration intensity will be included for improving the user experience. 

## :link: Outline

The project mainly consists of three layers - datastore, backend and frontend as shown in figure 1.
![](https://i.imgur.com/Pykm5H3.png)
<center>Figure 1 shows the system design.</center>
<br/>

The frontend app is built using Android, which interacts with the backend acoustic model via REST APIs written using flask microframework. The backend model uses training data in form of a CSV file to build the classifier. The CSV contains features extracted from sounds from the UrbanSound database.


### Step 1: Install :snake: python3 (v3.7.5) and pip3 (v19.3.1)

For macOS:
`brew install python3`
`curl https://bootstrap.pypa.io/get-pip.py -o get-pip.py`
`python3 get-pip.py`

For ubuntu:
`sudo apt-get install python3.7`
`sudo apt install python3-pip`

Also, install packages such as ffmpeg and libsndfile1.
`sudo apt-get install ffmpeg`
`sudo apt-get install libsndfile1`

### Step 2: Install android studio
Use [this](https://developer.android.com/studio) link to download android studio for running the app.


## :iphone:  User Interface
The following screenshots show the different UI screens in the application:

![](https://i.imgur.com/vY1Sjnd.png)


## :file_folder: File structure

The folder contains app and backend directories.
app contains the android code.
backend contains the backend service. It consists of the following folders:
* api: this contains driver.py file which has the APIs written. It has POST API for uploading audio and GET API for training the model.
* logs: stores logger.log to keep a track of application logs.
* models: 
    * datasetfold1.csv: csv file for training the model.
    * feature_extraction.py: contains feature extraction logic i.e extracts features from the wav files and writes them to a CSV file. Please change the folder paths in case using this file.
    * model.py: it contains the train and test methods for building a decision tree classifier.
    * model_tree.obj: contains the pickled decision tree model.
    * tester.py: generates a csv record of uploaded audio during testing phase (when android clients send requests).
* services: conatins sound_service.py. It helps in converting the uploaded 3gp audio into wav format and then processes it further.
* test: all the audio recordings uploaded from the app reside here.

> ec2: folder ec2 contains backend application code that can be directly pushed to any cloud service provider. Just run `python3 driver.py` and the flask app will start at port 80 by default.

## :rocket: Starting the application

### Backend flask app
Install the required packages as mentioned in the <b>requirements.txt</b> in the backend folder.

Run `pip3 install -r requirements.txt`.

Start the flask app by navigating to backend folder. From the api folder run the following commands - 

`export FLASK_APP=driver.py`
`python3 -m flask run`

This will start the backend app at port 5000 (by default).

<b>Fog server</b>
Set up a fog server using ngrok. Download ngrok from [this](https://ngrok.com/download) link.

Once ngrok is setup, run the following command from your terminal.
`./ngrok http 5000`
This exposes port 5000 to external world using public IP given by the tunnel.


### Frontend app
Open the app folder in android studio. Change the url in UploadTask class in MainActivity.java. Replace it by one given by ngrok tunnel.
Run the app on an android device that supports API level 22+.


## :white_check_mark: Testing the application
- Start by choosing alert mechanism by clicking on Select Options from the home screen.
- Select the alert modes as needed. You can select more than one alert mode.
- Based on your selection, choose appropriate controls from the home screen. eg. vibration intensity and color of flash screen.
- Now, click on sense to record audio around you. Give permissions to record audio and use file storage.
- Once audio is sensed (by default it is done for 5 seconds as configured in the application), ckick on upload button. 
- The app sends a request to backend server, which tests the audio uploaded against the model learnt (decision tree classifier).
- Once the audio is processed, a response indicating presence of alarm is returned (0 or 1).
- Once the application receives response from the backend server, it alerts the user if an alarm is present (as computed by the backend model), otherwise ignores the response sent. 

>:pushpin: You can use an ambulance, firetruck or a police siren for testing purpose.
