from models.model import Model

class SoundService():
    def __init__(self):
        self.model        = Model()
        self.preprocessor = PreProcessor()

        self.logger = open("../logs/logger.log", "a")

    """
    To do: change once app integration is complete
    """
    def analyze(self):
        self.model.train()
        label = self.model.test()
        if label == "0":
            return False
        else:
            return True

     def train(self):
        self.model.train()