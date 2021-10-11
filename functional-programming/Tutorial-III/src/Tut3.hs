module Tut3 where
import Data.Char (toUpper)

name, idno, username :: String
name      =  "Myself, Me"  -- replace with your name
idno      =  "01234567"    -- replace with your student id
username  =  "memyselfi"   -- replace with your TCD username


declaration -- do not modify this
 = unlines
     [ ""
     , "@@@ This exercise is all my own work."
     , "@@@ Signed: " ++ name
     , "@@@ "++idno++" "++username
     ]

data BinTree a
  = Empty
  | Node (BinTree a) a (BinTree a)
  deriving (Eq, Show)

one :: int
one = 1

{- Part 1
Write `binsert` to insert items into a BinTree
-}

-- insert val into Empty tree
binsert val Empty = Node Empty val Empty
binsert new (Node left old right)
  | new == old = Node left new right
  | new < old = Node (binsert new left) old right
  | otherwise = Node left old (binsert new right)

{- Part 2
Write `blookup` to search a BinTree
-}

-- lookup wanted in an Empty tree
blookup wanted Empty = False
bloopup wanted (Node left val right)
  | wanted < val = blookup wanted left
  | wanted > val = blookup wanted right
  | otherwise = True 

{- Part 3
Write `tpose` to transpose a list of lists
-}

-- like matrix transpose
tpose = error "tpose NYI!"
