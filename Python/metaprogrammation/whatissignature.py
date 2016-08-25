from inspect import Signature,Parameter
def make_sig(*names):
    parms = [Parameter(name,Parameter.POSITIONAL_OR_KEYWORD)
             for name in names]
    return Signature(parms)

class Structure:
    __signature__ = make_sig()
    def __init__(self,*args,**kwargs):
        bound_values = self.__signature__.bind(*args,**kwargs)
        for name, value in bound_values.arguments.items():
            setattr(self,name,value)

class Stock(Structure):
    __signature__ = make_sig('name','shares','price')

class Point(Structure):
    __signature__ = make_sig('x','y')

import inspect
print(inspect.signature(Stock))
s1 = Stock('ACME',400,490.1)
print(s1.name)
print(s1.shares)
print(s1.price)
# s2 = Stock('ACME',100)
