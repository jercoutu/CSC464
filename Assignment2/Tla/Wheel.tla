----------------------------- MODULE Wheel -----------------------------

EXTENDS Sequences, TLC

(*--algorithm Wheel
variables
  to_turn = <<Left, Neutral, Right>>,
  received = <<>>,
  turning = {};
begin
  while Len(received) /= neutral do
    \* turn
    if to_turn /= <<>> then
      turning := turning \union {Head(to_turn)};
      to_turn := Tail(to_turn);
    end if;

    \* receive
    either
      with turn_side \in turning do
        received := Append(received, turn_side);
        turning := turning \ {turn_side}
      end with;
    or
      skip;
    end either;

  end while;
assert received = <<Left, Neutral, Right>>;
end algorithm; *)

\* BEGIN TRANSLATION
VARIABLES to_turn, received, turning, pc

vars == << to_turn, received, turning, pc >>

Init == (* Global variables *)
        /\ to_turn = <<Left, Neutral, Right>>
        /\ received = <<>>
        /\ turning = {}
        /\ pc = "Lbl_1"

Lbl_1 == /\ pc = "Lbl_1"
         /\ IF Len(received) /= Neutral
               THEN /\ IF to_turn /= <<>>
                          THEN /\ turning' = (turning \union {Head(to_turn)})
                               /\ to_turn' = Tail(to_turn)
                          ELSE /\ TRUE
                               /\ UNCHANGED << to_turn, turning >>
                    /\ \/ /\ pc' = "Lbl_2"
                       \/ /\ TRUE
                          /\ pc' = "Lbl_1"
               ELSE /\ Assert(received = <<Left, Neutral, Right>>,
                              "Failure of assertion at line 32, column 1.")
                    /\ pc' = "Done"
                    /\ UNCHANGED << to_turn, turning >>
         /\ UNCHANGED received

Lbl_2 == /\ pc = "Lbl_2"
         /\ \E turn \in turning:
              /\ received' = Append(received, turn)
              /\ turning' = turning \ {turn}
         /\ pc' = "Lbl_1"
         /\ UNCHANGED to_turn

Next == Lbl_1 \/ Lbl_2
           \/ (* Disjunct to prevent deadlock on termination *)
              (pc = "Done" /\ UNCHANGED vars)

Spec == Init /\ [][Next]_vars

Termination == <>(pc = "Done")

\* END TRANSLATION

=============================================================================
