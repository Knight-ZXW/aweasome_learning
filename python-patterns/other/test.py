class A():
    name = 'hello'

class B(A):
    pass
b = B()
print(b.name)
print(hasattr(b,'name'))