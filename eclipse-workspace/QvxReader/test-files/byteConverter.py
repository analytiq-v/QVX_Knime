def printLongValue():
    inFile = open("byteConverterIn.txt")
    total = 0
    lines = inFile.readlines()
    if len(lines) != 8:
        print("8 bytes were expected, but", len(lines) + "bytes provided")
    for i in range(8):
        total += int(lines[i])*256**i
    return total

        
        
