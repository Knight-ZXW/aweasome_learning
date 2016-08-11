import re

def luhn_check(num):
    """Number - List of reversed digits"""
    ''' Number - List of reversed digits '''
    digits = [int(x) for x in reversed(str(num))]
    check_sum = sum(digits[::2]) + sum((dig // 10 + dig % 10) for dig in [2 * el for el in digits[1::2]])
    return check_sum % 10 == 0


def isValidVisaCard(number):
    pattern = '^4\d{15}'
    res =re.compile(pattern).match(number)
    if res and luhn_check(number):
        return True
    else:
        return False


def isValidAmExCard(number):
    normal_Amex_patter = '^37\d{13}'

    Gold_Amex_patter = '^3\d{21}'
    if re.match(normal_Amex_patter,number) or (re.match(Gold_Amex_patter,number)and luhn_check(number)):
        return True
    else:
        print('is else')
        return False


def idValidDinersCard(number):
    pattern = '^3\d{13}'
    res = re.compile(pattern).match(number)
    if res and luhn_check(number):
        return True
    else:
        return False


def idValidJCBCard(number):
    pattern = '^35\d{14}'
    res = re.compile(pattern).match(number)
    if res and luhn_check(number):
        return True
    else:
        return False


def isValidMasterCard(number):
    pattern = '^5\d{15}'
    res = re.compile(pattern).match(number)
    if res and luhn_check(number):
        return True
    else:
        return False


CREDIT_CARD_TYPE = ('Visa', 'Master', 'AmEx', 'Diners', 'JCB')
CREDIT_CARD_CHECK_METHOD = (isValidVisaCard,isValidMasterCard,isValidAmExCard,idValidDinersCard,idValidJCBCard)


class NotSupportCardType(BaseException):
    pass


def isValidCard(number,type):
    if type not in CREDIT_CARD_TYPE:
        raise NotSupportCardType
    check_method = CREDIT_CARD_CHECK_METHOD[CREDIT_CARD_TYPE.index(type)]
    return check_method(number)

print(luhn_check('4218709881418384'))
print(isValidVisaCard('4218709881418384'))
# print(isValidMasterCard('5224751181803864'))
# print(isValidAmExCard('3252461085113627468541'))
# print(idValidDinersCard('36535576018678'))
# print(idValidJCBCard('3560776576706155'))
# print(isValidCard('4533066005127712','Visa'))
