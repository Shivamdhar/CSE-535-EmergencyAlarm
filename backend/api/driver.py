from flask import Flask, request, jsonify
from flask_cors import CORS, cross_origin
from werkzeug import secure_filename

import datetime
import logging
import os

from backend.services.sound_service import SoundService

DOWNLOAD_FOLDER = "../logs/"
UPLOAD_FOLDER = "../recordings/"

LOG_FILE = "logger" + str(datetime.datetime.now()) + ".log"

app = Flask(__name__)
cors = CORS(app)

app.config["UPLOAD_FOLDER"] = UPLOAD_FOLDER
app.config["DOWNLOAD_FOLDER"] = DOWNLOAD_FOLDER

file_handler = logging.FileHandler(DOWNLOAD_FOLDER + "logger.log")
logger = logging.getLogger(__name__)
logger.setLevel(logging.INFO)
logger.addHandler(file_handler)

sound_service = SoundService()

@app.route("/")
def home():
    app.logger.info("[api-home]")
    return "Home"

@app.route("/api/v1/train", methods=["GET"])
def train_models():
    global sound_service
    sound_service.train()

@app.route("/api/v1/upload", methods=["POST"])
@cross_origin()
def upload_recording():
    global sound_service

    if "files[]" not in request.files:
        return "Please send the recording"

    uploaded_files = request.files.getlist("files[]")
    app.logger.info("[api-upload] recording received: " + str(uploaded_files))

    for file in uploaded_files:
        filename = os.path.join(app.config["UPLOAD_FOLDER"], secure_filename(file.filename))
        file.save(filename)
        sound_service.analyze(filename)

    return True #if emergency sound present in the recording

if __name__ == "__main__":
    app.run(debug=True)