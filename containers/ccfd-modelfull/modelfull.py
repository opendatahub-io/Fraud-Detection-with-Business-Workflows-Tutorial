import joblib
import pandas as pd

class modelfull(object):
 
    def __init__(self):
        print("Initializing")
        # Variables needed for metric collection
        self.V3=0
        self.V4=0
        self.V10=0
        self.V11=0
        self.V12=0
        self.V14=0
        self.V17=0
        self.Amount=0
        self.proba_1 = 0
        #///////// Loading mode from filesystem
        self.clf = joblib.load('modelfull.pkl')
        print("Model uploaded to class")
 
    def predict(self,x,features_names):
        print(x)
        print(type(x))
        result = "PASS"
        featurearray=[float(i) for i in x.split(',')]
        print(featurearray)
        # grabbing features for metric to be scraped by prometheus
        self.V3 = featurearray[0]
        self.V4 = featurearray[1]
        self.V10 = featurearray[2]
        self.V11 = featurearray[3]
        self.V12 = featurearray[4]
        self.V14 = featurearray[5]
        self.V17 = featurearray[6]
        self.Amount = featurearray[7]
        
        rowdf = pd.DataFrame([featurearray], columns = ['V3','V4','V10','V11','V12','V14','V17','Amount'])
        print(rowdf)
        self.proba_1 = self.clf.predict_proba(rowdf)[:,1]
        predictions = self.clf.predict(rowdf)
        # initialize list of lists
        return predictions
        
    def metrics(self):
        return [
            {"type":"GAUGE","key":"V3","value":self.V3},
            {"type":"GAUGE","key":"V4","value":self.V4},
            {"type":"GAUGE","key":"V10","value":self.V10},
            {"type":"GAUGE","key":"V11","value":self.V11},
            {"type":"GAUGE","key":"V12","value":self.V12},
            {"type":"GAUGE","key":"V14","value":self.V14},
            {"type":"GAUGE","key":"V17","value":self.V17},
            {"type":"GAUGE","key":"Amount","value":self.Amount},
            {"type":"GAUGE","key":"proba_1","value":self.proba_1[0]},
            ]
