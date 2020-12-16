# ccfd-seldon-usertask-model

Seldon model trained with the [Kaggle credit card fraud dataset](https://www.kaggle.com/mlg-ulb/creditcardfraud).

To build the model:

```shell
s2i build . seldonio/seldon-core-s2i-python3:1.2.2 odh-workshops/ccfd-business-workflow-tutorial-ccfd-seldon-usertask-model:tag
```
