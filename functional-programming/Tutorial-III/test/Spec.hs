{-# LANGUAGE StandaloneDeriving #-}

module Main where

import Test.HUnit
import Test.Framework as TF (defaultMain, testGroup, Test)
import Test.Framework.Providers.HUnit (testCase)
-- import Test.Framework.Providers.QuickCheck2 (testProperty)

import Tut3

main = defaultMain tests -- runs the tests

tests :: [TF.Test]
tests = [ testGroup "\nTutorial 03 Tests\n"
            [ insertTests
            , lookupTests
            , transposeTests
           ]
        ]


insertTests :: TF.Test
insertTests
 = testGroup "\nPart 1 - Insertions\n"
   --  [ testCase "What?" (1+1  @?= (3::Int))
   --  ]
   [ testCase "Insert into Empty" (binsert (42::Int) Empty @?= Node Empty 42 Empty)
   , testCase "Insert into Node (Equal)" 
      (binsert (42:Int) (Node Empty 42 Empty)
      @?=
      (Node Empty 42 Empty))
   , testCase "Insert into Node (Lesser)" 
      (binsert (42:Int) (Node Empty 99 Empty)
      @?=
      (Node (Node Empty 42 Empty) 99 Empty))
   , testCase "Insert into Node (Greater)" 
      (binsert (42:Int) (Node Empty 0 Empty)
      @?=
      (Node Empty 0 (Node Empty 42 Empty))
   ]

lookupTests :: TF.Test
lookupTests
 = testGroup "\nPart 2 - Lookups\n"
    [ testCase "Lookup Empty" (blookup (42::Int) Empty  @?= False)
    , testCase "Lookup Left"
      ( blookup (42::Int) (Node (Node Empty 42 Empty) 99 Empty) 
         @?= True
      )
    ]

transposeTests :: TF.Test
transposeTests
 = testGroup "\nPart 3 - Transpose\n"
    [ testCase "Why?" (42+99   @?= (0::Int))
    ]
