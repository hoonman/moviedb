def get_averages(path: str):
    data_count = 0
    jdbc_total = 0
    search_servlet_total = 0
    with open(path, 'r') as file:
        for line in file:
            jdbc, search = line.split(", ")
            jdbc_time = int(jdbc.split(":")[1])
            search_time = int(search.split(":")[1])
            jdbc_total += jdbc_time
            search_servlet_total += search_time
            data_count +=1
    return jdbc_time/data_count, search_servlet_total/data_count
if __name__ == "__main__":
    avgs = get_averages("/Users/jasonwong/Documents/School/CS122b/fabflix/WebContent/WEB-INF/Logger/search.txt")
    print("JDBC average time is:", avgs[0])
    print("Servelt time is:", avgs[1])
