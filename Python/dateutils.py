import datetime
str = '2016-08-10'
date = datetime.date(2016,8,10)
print(date.strftime('%Y-%m-%d'))

print( isinstance(str,datetime.date))