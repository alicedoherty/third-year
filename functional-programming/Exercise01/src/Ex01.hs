module Ex01 where
import Data.Char (toUpper)

name, idno, username :: String
name      =  "Alice Doherty"  -- replace with your name
idno      =  "19333356"    -- replace with your student id
username  =  "aldohert"   -- replace with your TCD username


declaration -- do not modify this
 = unlines
     [ ""
     , "@@@ This exercise is all my own work."
     , "@@@ Signed: " ++ name
     , "@@@ "++idno++" "++username
     ]


{- Part 1

Write a function 'raise' that converts a string to uppercase

Hint: 'toUpper :: Char -> Char' converts a character to uppercase
if it is lowercase. All other characters are unchanged.
It is imported should you want to use it.

-}
raise :: String -> String
raise = map toUpper

{- Part 2

Write a function 'nth' that returns the nth element of a list.
Hint: the test will answer your Qs

-}
nth :: Int -> [a] -> a
nth i xs = xs !! (i - 1)

{- Part 3

Write a function `commonLen` that compares two sequences
and reports the length of the prefix they have in common.

-}
commonLen :: Eq a => [a] -> [a] -> Int
commonLen (x:xs) (y:ys) | x == y = commonLen xs ys + 1
commonLen _ _ = 0 

{- Part 4

(TRICKY!) (VERY!)

Write a function `runs` that converts a list of things
into a list of sublists, each containing elements of the same value,
which when concatenated together give the same list

So `runs [1,2,2,1,3,3,3,2,2,1,1,4]`
 becomes `[[1],[2,2],[1],[3,3,3],[2,2],[1,1],[4]]`

Hint:  `elem :: Eq a => a -> [a] -> Bool`

HINT: Don't worry about code efficiency
       Seriously, don't!

-}

runs :: Eq a => [a] -> [[a]]
runs [] = []
runs [x] = [[x]]
runs (x:y:ys) = let (head:tail) = runs (y:ys)
                 in if x /= y
                    then [x]:head:tail
                    else (x:head):tail