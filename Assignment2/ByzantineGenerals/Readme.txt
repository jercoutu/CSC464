Problem:
The byzantine generals problem by lamport is about reaching a consensus of decision, whether to attack or to retreat. The commander takes a decision as to what he wants to do, and then the respective lieutenants need to pick whether they vote to agree or retreat. A loyal lieutenant always goes against the current vote, while a loyal lieutenant agrees with the decision. If there is a tie in the votes, then the vote is a tie result and no action is taken! The best case situation is that there are no traitors in the lieutenants, causing for the generals decision to be taken every time.

Expected output:
As stated above, when there are no traitors the 12 gens 0 traitor output should have reached a consensus, however the output does not demonstrate that a consensus has been reached. Although the commander says that he wants to attack, the output is still that the lieutenants vote not to attack, resulting in a false answer. This occurs the same way for every number of traitors allowed, as shown in the test results for 1,2,3 traitors out of 12, the maximum number allowed using the n > 3*m. 

Nature of the failure:
Part of the potential failure is that I may not be tracking the code as well as the original author. While the outputted println of “We are attacking this round T or F “ (93, generals.java) attempts to output whether or not we have concluded to attack or retreat, the decision always outputs a failure. While I am attempting to output this after the decision_this_round has been modified to show if a consensus has been reached, there seems to never be one. I was not able to find any instances where there was a consensus reached, as shown through the supplied outputs. I know that the messages is passed, as shown through the various message tables when process [x] receiveRound(i) -> display what message is passed by which general in the list.

Source code:
https://github.com/dmpe/Homeworks5/tree/master/Assignment6/src/JavaByzantine
