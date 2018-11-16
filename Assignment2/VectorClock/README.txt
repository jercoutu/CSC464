
VECTOR CLOCK IMPLEMENTATION

Simplistic implementation of a vector clock. Program attempts to synchronize different users and their schedules in order to achieve a coinciding day to meet up, which in this case is done by meeting on thursday. The clocks each have their own internal clock with individual "timestamps" as they progress through their indivial processes. When a message is sent,the message is accompanied by the sender's clock number and the receiver compares the two numbers, keeping the greater number and incrementing it. Nothing is returned to sender, so the numbers do not necessarily match as the various channels keep running. At the end, it is the data that is important for this implication and not the clock "time".


Results from test
Dave's data 	 Thursday map[dave:4 alice:1 ben:2 cathy:2]
Cathy's data 	 Thursday map[cathy:3 alice:1 dave:4 ben:2]
Bens data 	   Thursday map[ben:4 alice:3 dave:4 cathy:3]
Alice data 	   Thursday map[cathy:3 alice:3 ben:3 dave:4]
PASS

the result is a pass as it has all of the users meeting on the same date (Thursday) and the outputted map shows what numbers are received from each user through the communications.

Src: https://github.com/sankalpjonn/vectorclock/blob/master/vectorclock.go
 
