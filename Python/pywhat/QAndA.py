# x,y = ???
# x +y == y+x = 》YES
x = [1]
y = [2]
print(x + y == y + x)

# x = ???
# x < x
# True  = 》 IMPOSSIBLE

##
x,y = [],[1]
for a,b in zip(x,y):
    print('a {} b{}'.format(a,b))