- # General
    - #### Team#:
    
    - #### Names:
    
    - #### Project 5 Video Demo Link:

    - #### Instruction of deployment:

    - #### Collaborations and Work Distribution:


- # Connection Pooling
    - #### Include the filename/path of all code/configuration files in GitHub of using JDBC Connection Pooling.
    
    - #### Explain how Connection Pooling is utilized in the Fabflix code.
    
    - #### Explain how Connection Pooling works with two backend SQL.
    

- # Master/Slave
    - #### Include the filename/path of all code/configuration files in GitHub of routing queries to Master/Slave SQL.

    - #### How read/write requests were routed to Master/Slave SQL?
    

- # JMeter TS/TJ Time Logs
    - #### Instructions of how to use the `log_processing.*` script to process the JMeter logs.


- # JMeter TS/TJ Time Measurement Report

| **Single-instance Version Test Plan**          | **Graph Results Screenshot**               | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|--------------------------------------------|----------------------------|-----------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![](img/single-instance-http-1thread.png)  | 229                        | 2.1949975322213801654                                  | 0.0016876                 | ??           |
| Case 2: HTTP/10 threads                        | ![](img/single-instance-http-10thread.png) | 309                        | 4.1478421355178829444                                | 0.0014308                 | ??           |
| Case 3: HTTPS/10 threads                       | ![](path to image in img/)                 | 727                        | 2.036079069812229303                                | 0.00022805                | ??           |
| Case 4: HTTP/10 threads/No connection pooling  | ![](path to image in img/)                 | ??                         | ??                                | ??                        | ??           |

| **Scaled Version Test Plan**                   | **Graph Results Screenshot**               | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|--------------------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![](img/scaled-instance-http-1thread.png)  | 150                        | 1.960823869521410634                                  | 0.0008266                        | ??           |
| Case 2: HTTP/10 threads                        | ![](img/scaled-instance-http-10thread.png) | 144                        | 2.2099638043900071693                                  | 0.00029919                       | ??           |
| Case 3: HTTP/10 threads/No connection pooling  | ![](path to image in img/)                 | ??                         | ??                                  | ??                        | ??           |
