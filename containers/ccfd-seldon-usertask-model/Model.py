import joblib

class Model(object):

    def __init__(self):
        print("Initializing.")
        print("Loading model.")
        self.model = joblib.load('model.pkl')

    def predict(self, X, features_names):
        print(X)
        return self.model.predict_proba(X)