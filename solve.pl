#!/usr/bin/perl
# Author: Adam Bliss <abliss@gmail.com>
# Date: 2014-03-06
# Purpose: compute an equilibrium mixed strategy (19 probabilities) for 1 street
# We will be computing the value of the game as a linear polynomial in 19*4
# variables.

use strict;

# The names of the 19 variables and their indices when stored in an array.
our ($KF, $KC, $KRF, $KRC, $KR4, $BF, $BC, $B3F, $B3C,
     $K, $_BF, $_BC, $_B3F, $_B3C, $F, $C, $RF, $RC, $R4) = (0..18);

# The names of the hands
our ($QQ, $KK, $AA, $JJ) = (0..3);

# Initial pot size in bets
our $POT = 10.0;

# prob(hand1, hand2) returns the probability that p1 has hand1 and p2 has hand2.
sub prob {
    my $h1 = shift;
    my $h2 = shift;
    # There are 21 total two-card hands; 6 each of QQ-AA, and 3 of JJ.
    my @counts = (6.0, 6.0, 6.0, 3.0);
    my $sum = 21.0;
    my $p = $counts[$h1];
    if ($h2 != $h1) {
        $p *= $counts[$h2];  # independent
    } elsif ($h1 == 3) {
        $p = 0;   # can't both have JJ
    } else {
        $p *= 1;  # villian has the other identical pocket pair
    }
    return $p / ($sum * ($sum - 1));   
}

# valOf(s, p) computes the value of a strategy against itself. Assumes p = the
# pot and s = (0, 0.5, 1) if p1 is winning, chopping, or losing. Returns a
# polynomial in the 19 variables of the strategy, as an arrayRef of
# monomials, each stored as [varNum, varNum, coefficient]
sub valOf {
    my $s = shift;
    my $p = shift;
    return [
        [$KF,  $K,   $s*$p],
        [$KF,  $_BF,  0],
        [$KF,  $_BC,  0],
        [$KF,  $_B3F, 0],
        [$KF,  $_B3C, 0],
        [$KC,  $K,   $s*$p],
        [$KC,  $_BF,  $s*($p+2)-1],
        [$KC,  $_BC,  $s*($p+2)-1],
        [$KC,  $_B3F, $s*($p+2)-1],
        [$KC,  $_B3C, $s*($p+2)-1],
        [$KRF, $K,   $s*$p],
        [$KRF, $_BF,  $p+1],
        [$KRF, $_BC,  $s*($p+4)-2],
        [$KRF, $_B3F, -2],
        [$KRF, $_B3C, -2],
        [$KRC, $K,   $s*$p],
        [$KRC, $_BF,  $p+1],
        [$KRC, $_BC,  $s*($p+4)-2],
        [$KRC, $_B3F, $s*($p+6)-3],
        [$KRC, $_B3C, $s*($p+6)-3],
        [$KR4, $K,   $s*$p],
        [$KR4, $_BF,  $p+1],
        [$KR4, $_BC,  $s*($p+4)-2],
        [$KR4, $_B3F, $p+3],
        [$KR4, $_B3C, $s*($p+8)-4],

        [$BF,  $F,   $p],
        [$BF,  $C,   $s*($p+2)-1],
        [$BF,  $RF,  -1],
        [$BF,  $RC,  -1],
        [$BF,  $R4,  -1],
        [$BC,  $F,   $p],
        [$BC,  $C,   $s*($p+2)-1],
        [$BC,  $RF,  $s*($p+4)-2],
        [$BC,  $RC,  $s*($p+4)-2],
        [$BC,  $R4,  $s*($p+4)-2],
        [$B3F, $F,   $p],
        [$B3F, $C,   $s*($p+2)-1],
        [$B3F, $RF,  $p+2],
        [$B3F, $RC,  $s*($p+6)-3],
        [$B3F, $R4,  -3],
        [$B3C, $F,   $p],
        [$B3C, $C,   $s*($p+2)-1],
        [$B3C, $RF,  $p+2],
        [$B3C, $RC,  $s*($p+6)-3],
        [$B3C, $R4,  $s*($p+8)-4],
        ];
}

my @valsOf = (valOf(0.0, $POT), valOf(0.5, $POT), valOf(1.0, $POT));
# coefficient matrix with [p1Hand][p1Strat][p2Hand][p2Strat] -- 19*4=76 vars
my $poly = [];
foreach my $p1Hand ($QQ..$JJ) {
    foreach my $p2Hand ($QQ..$JJ) {
        my $p = prob($p1Hand, $p2Hand);
        my $cmp = ($p1Hand <=> $p2Hand) + 1;
        my $termsArr = $valsOf[$cmp];
        foreach my $term (@$termsArr) {
            $poly->[$p1Hand]->[$term->[0]]->[$p2Hand]->[$term->[1]] +=
                $p * $term->[2];
        }
    }
}

if(0) { #XXX
# poly is now a triangular matrix; we will double it, making it represent 2V, by
# reflecting across the diagonal. This turns each row into a partial derivative.
foreach my $p1Hand ($QQ..$JJ) {
    foreach my $p1Strat ($KF..$B3C) {
        foreach my $p2Hand ($QQ..$JJ) {
            foreach my $p2Strat ($K..$R4) {
                $poly->[$p2Hand]->[$p2Strat]->[$p1Hand]->[$p1Strat] = 
                    $poly->[$p1Hand]->[$p1Strat]->[$p2Hand]->[$p2Strat];
            }
        }
    }
}
}

# Now for any hand h and strat s, $poly->[h]->[s] is a polynomial in 76
# variables giving the partial derivative of V with respect to variable h/s.
# But these variables are not independent; to increase one we must decrease its
# exclusive partners. So we now build the "projected partials" by subtracting
# those exclusives.
my $p1TotalsByHand = [];       # p1's 9 strats must sum to 1
my $p2BettedTotalsByHand = []; # p2's 5 betted-strats must sum to 1
my $p2CheckedTotalsByHand = [];# p2's 5 checked-strats must sum to 1
my $totalCounts = [];
foreach my $p1Hand ($QQ..$JJ) {
    foreach my $p1Strat ($KF..$B3C) {
        foreach my $p2Hand ($QQ..$JJ) {
            foreach my $p2Strat ($K..$R4) {
                my $c = $poly->[$p1Hand]->[$p1Strat]->[$p2Hand]->[$p2Strat];
                $p1TotalsByHand->[$p1Hand]->[$p2Hand]->[$p2Strat] += $c;
            }
            foreach my $p2Strat ($K..$_B3C) {
                my $c = $poly->[$p1Hand]->[$p1Strat]->[$p2Hand]->[$p2Strat];
                $p2BettedTotalsByHand->[$p2Hand]->[$p1Hand]->[$p1Strat] += $c;
            }
            foreach my $p2Strat ($F..$R4) {
                my $c = $poly->[$p1Hand]->[$p1Strat]->[$p2Hand]->[$p2Strat];
                $p2CheckedTotalsByHand->[$p2Hand]->[$p1Hand]->[$p1Strat] += $c;
            }
        }
    }
}
           
# To compute the projected partial for variable i, part of a group of n vars
# which must sum to 1, we take the partial for i, multiply by (1+n)/n, and
# subtract (1/n) of the sum of the partials for the whole group.
my $projected = [];
foreach my $p1Hand ($QQ..$JJ) {
    foreach my $p1Strat ($KF..$B3C) {
        foreach my $p2Hand ($QQ..$JJ) {
            foreach my $p2Strat ($K..$R4) {
                my $c = $poly->[$p1Hand]->[$p1Strat]->[$p2Hand]->[$p2Strat];
                $c *= 10.0 / 9.0;
                $c -= $p1TotalsByHand->[$p1Hand]->[$p2Hand]->[$p2Strat]/
                    9.0;
                $projected->[$p1Hand]->[$p1Strat]->[$p2Hand]->[$p2Strat] = $c;
            }
            foreach my $p2Strat ($K..$_B3C) {
                my $c = $poly->[$p1Hand]->[$p1Strat]->[$p2Hand]->[$p2Strat];
                $c *= 6.0 / 5.0;
                $c -= $p2BettedTotalsByHand->[$p2Hand]->[$p1Hand]->[$p1Strat]/ 
                    5.0;
                $projected->[$p2Hand]->[$p2Strat]->[$p1Hand]->[$p1Strat] = $c;
            }
            foreach my $p2Strat ($F..$R4) {
                my $c = $poly->[$p1Hand]->[$p1Strat]->[$p2Hand]->[$p2Strat];
                $c *= 6.0 / 5.0;
                $c -= $p2CheckedTotalsByHand->[$p2Hand]->[$p1Hand]->[$p1Strat]/ 
                    5.0;
                $projected->[$p2Hand]->[$p2Strat]->[$p1Hand]->[$p1Strat] = $c;
            }
        }
    }
}


# Dump the equations
my @names = qw(KF KC KRF KRC KR4 BF BC _B3F B3C K _BF _BC _B3F _B3C F C RF RC R4);
if(0) {
    foreach my $hHand ($QQ..$JJ) {
        foreach my $hStrat ($KF..$R4) {
            printf("Projected v%d/%-3s: 0 = ", $hHand, $names[$hStrat]);
            foreach my $vHand ($QQ..$JJ) {
                foreach my $vStrat ($K..$R4) {
                    my $c = $projected->[$hHand]->[$hStrat]->[$vHand]->[$vStrat];
                    if ($c != 0) {
                        printf("%+1.3f v%d/%-3s ", $c, $vHand, $names[$vStrat]);
                    }
                }
            }
        }
    }
}


# We now have the system of equations. Flatten into one matrix.
my $coeff = [];
my $result = [];
my $row = 0;
foreach my $hHand ($QQ..$JJ) {
    foreach my $hStrat ($KF..$R4) {
        $result->[$row] = [0];
        foreach my $vHand ($QQ..$JJ) {
            foreach my $vStrat ($KF..$R4) {
                my $col = $vHand * 19 + $vStrat;
                my $c = $projected->[$hHand]->[$hStrat]->[$vHand]->[$vStrat];
                $coeff->[$row]->[$col] = $c;
            }
        }
        $row++;
    }
}
# Append the constraints that certain things must add to 1
foreach my $hHand ($QQ..$JJ) {
    $result->[$row] = [1];
    foreach my $p1Strat ($KF..$B3C) {
        my $col = $hHand * 19 + $p1Strat;
        $coeff->[$row]->[$col] = 1;
    }
    $row++;
    $result->[$row] = [1];
    foreach my $p2Strat ($K..$_B3C) {
        my $col = $hHand * 19 + $p2Strat;
        $coeff->[$row]->[$col] = 1;
    }
    $row++;
    $result->[$row] = [1];
    foreach my $p2Strat ($F..$R4) {
        my $col = $hHand * 19 + $p2Strat;
        $coeff->[$row]->[$col] = 1;
    }
    $row++;
}

# even out size of rows
foreach my $i (0..$row-1) {
    foreach my $j (0..$JJ*19+$R4) {
        $coeff->[$i]->[$j] += 0;
    }
}

# now actually try to solve, following the perldoc
print "Solving: ", scalar(localtime),"\n";
use Math::MatrixReal;
my $A = Math::MatrixReal->new_from_rows($coeff);

$A->display_precision(3);
print $A;
exit;
my $LR = $A->decompose_LR();
my $b = Math::MatrixReal->new_from_rows($result);
my ($dim, $x, $B);
if (($dim, $x, $B)= $LR->solve_LR($b)) {
    print "Solved! dim = $dim\n\n";
    foreach my $hHand ($QQ..$JJ) {
        foreach my $hStrat ($KF..$R4) {
            my $row = $hHand * 19 + $hStrat;
            printf("Prob of v%d/%-3s: %.5f\n", $hHand, $names[$hStrat], $x->[$row]->[0]);
        }
    }    
} else {
    print "No solutions. :(";
}
