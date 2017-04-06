Insight Data Engineering Coding Challeng Submission
Name: Colin Biafore

Notes:

./run.sh will compile, run, and clean the program. The basic test case passes. 

The code is commented.

Features 1,2 and 4 all work with a large input. 

Feature 3 does not work, sadly... I've written a small explanation below on why. 
I noticed on the day of the deadline that someone had updated the test cases in the github repo. I didn't understand why the hours.txt log output was different - and then I read the prompt a little more closely ---- "A 60-minute window can be any 60 minute long time period, windows don't have to start at a time when an event occurs." As it turns out, this line is very important. My original implementation clocked at around 34 seconds for the large data set when not considering ALL 60 minute intervals. It only recorded a new interval when a log line came in (The original test in the repo would have passed). The implementation was quite nice. A LinkedList was used as a Queue to continuously add new LogData and remove from the head of the queue so that the LinkedList always had a 60 minute window. While I do think accounting for every possible 60 minute window is possible with this implementation - I did not have enough time to implement it properly as I'm not at my best when I have to scramble. The best I could come up with is a basic sliding window that combs over a very large array of seconds and gets the value of all possible intervals. It might work, but I've never waited for it to terminate. It looks like it would take at least an hour. This is funny considering the day before submission I thought I was finished and wrote an entire Date Formatting utility to cut almost 40 seconds off of the runtime. 

Other issues: 
I also know that even if I had implemented the third feature properly - my program is very memory inefficient. I copy my HashMaps to arrays and then use MergeSort (which makes a temporary copy of the arrays, again). If I were to do this over again I would A) Consistently check the repository for updates (and read the prompt more closely - that's on me for not understanding what ANY 60 minute long period means) and B) I would use QuickSort to sort the arrays, more specifically Quick Select, because I don't actually need to sort the whole array, I just need to find the top N elements for features 1,2, and 3. This would give me O(n) selection of the top X elements and I would avoid the shadow copy used in Merge Sort. Quick Select is implemented by setting a pivot in the center (not randomly) of the array and recursively swapping elements on right and left that are greater or less than the pivot value until the pivot is placed X away from the end of the array. Then we have the top X elements. 

The code in AnalyticsUtility is also a bit repetitive. I would have liked to make the Host, Resource, and Interval all extend from a base class with an id and count instance variable. Doing so would allow me to only write a basic function that sorts something of the base type and writes it to a log that gets specified. This would have made the utility much more extendable.

Other features (wishlist):
If the submission day had not been spent scrambling - here is what I had planned on adding: 
1) A list of hosts with the most blocked attempts - they might need to be dealt with differently or monitored more closely by the system. It would also be interesting to see what resources they are accessing or what type of requests they are putting in. I might check to see if they submitting strange web requests, checking for things like a CRLF attack or XSS in the request. 

2) I wanted to make the entire application mock a client server by feeding the input to a client thread and then sending it to a server thread. This would make the application more usable. You could just send your log data to some IP/Port where my server is running and get the output sent back. I could see that being a real world application if I were working on an analytics server or something.

3) The ability to change the value of the number of items returned in features 1,2,3, as well as the number of attempts before logging an IP's request (feature 4). I wanted to have a configuration file that gets read when the application starts so the user can specify how much data should be written to each of the logs. 

4) The ability to change feature 3 to only show the top X NON-OVERLAPPING intervals. I noticed that the output (this was based on what I thought was the correct implementation) included all 60 minute intervals on July 13th around the same time. In a real world scenario, while having all possible intervals might be useful for more complex analytics - a user would probably be more interested in the busiest time of each day or the top ten busiest hours that don't overlap, or the top ten busiest days that don't overlap. The plan was to give the user power through configuration to change what goes into the hours.txt log file. They could change to "minutes", "hours", or "days" to get the top 10 of each that do not overlap with each other. 

