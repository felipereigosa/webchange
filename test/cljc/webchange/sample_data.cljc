(ns webchange.sample-data)

(def levels [{:name   "Level 1",
              :level  1,
              :scheme {:lesson {:name "Lesson", :lesson-sets ["concepts"]}, :assessment {:name "Assessment", :lesson-sets ["assessment-1"]}},
              :lessons
                      [{:name        "Lesson 1",
                        :type        "lesson",
                        :lesson      1,
                        :activities
                                     [{:activity "home", :time-expected 300 :unique-id 0}
                                      {:only ["beginner"], :activity "see-saw", :time-expected 300 :unique-id 1}
                                      {:activity "swings", :time-expected 300 :unique-id 2}
                                      {:activity "sandbox", :time-expected 300 :unique-id 3}
                                      {:scored        true,
                                       :activity      "volleyball",
                                       :tags-by-score {:advanced [75 101], :intermediate [0 75]},
                                       :time-expected 300
                                       :unique-id 4}
                                      {:activity "book", :time-expected 300 :unique-id 5}
                                      {:only          ["intermediate"],
                                       :scored        true,
                                       :activity      "cycling",
                                       :tags-by-score {:beginner [0 75], :intermediate [75 101]},
                                       :time-expected 300
                                       :unique-id 6}
                                      {:activity "painting-tablet", :time-expected 300 :unique-id 7}],
                        :lesson-sets {:concepts "ls1"}}
                       {:name        "Lesson 2",
                        :type        "lesson",
                        :lesson      2,
                        :activities
                                     [{:only ["beginner"] :activity "home", :time-expected 300 :unique-id 8}
                                      {:only ["beginner"], :activity "see-saw", :time-expected 300 :unique-id 9}
                                      {:activity "swings", :time-expected 300 :unique-id 10}
                                      {:activity "sandbox", :time-expected 300 :unique-id 11}
                                      {:scored        true,
                                       :activity      "volleyball",
                                       :tags-by-score {:advanced [75 101], :intermediate [0 75]},
                                       :time-expected 300
                                       :unique-id 12}
                                      {:activity "book", :time-expected 300 :unique-id 13}
                                      {:only          ["intermediate"],
                                       :scored        true,
                                       :activity      "cycling",
                                       :tags-by-score {:beginner [0 75], :intermediate [75 101]},
                                       :time-expected 300
                                       :unique-id 14}
                                      {:activity "painting-tablet", :time-expected 300 :unique-id 15}],
                        :lesson-sets {:concepts "ls2"}}
                       ]}
             {:name   "Level 2",
              :level  2,
              :scheme {:lesson {:name "Lesson", :lesson-sets ["concepts-single" "concepts-group"]}, :assessment {:name "Assessment", :lesson-sets ["assessment"]}},
              :lessons
                      [{:name   "Lesson 12",
                        :type   "lesson",
                        :lesson 1,
                        :activities
                                [{:activity "cinema", :time-expected 300 :unique-id 16}
                                 {:activity "cinema-video", :time-expected 300 :unique-id 17}
                                 {:activity "letter-intro", :time-expected 300 :unique-id 18}
                                 {:activity "park-poem", :time-expected 300 :unique-id 19}
                                 {:scored true, :activity "running", :time-expected 300 :unique-id 20}
                                 {:scored true, :activity "slide", :time-expected 300 :unique-id 21}
                                 {:activity "writing-lesson", :time-expected 300 :unique-id 22}
                                 {:activity "writing-practice", :time-expected 300 :unique-id 23}
                                 {:scored true, :activity "magic-hat", :time-expected 300 :unique-id 24}],
                        :lesson-sets
                                {:concepts-all    "concepts-all",
                                 :concepts-group  "l2-ls1-3",
                                 :concepts-single "l2-ls1-1"}}
                       {:name   "Lesson 13",
                        :type   "lesson",
                        :lesson 2,
                        :activities
                                [{:activity "cinema", :time-expected 300 :unique-id 25}
                                 {:activity "cinema-video", :time-expected 300 :unique-id 26}
                                 {:activity "letter-intro", :time-expected 300 :unique-id 27}
                                 {:activity "park-poem", :time-expected 300 :unique-id 28}
                                 {:scored true, :activity "running", :time-expected 300 :unique-id 29}
                                 {:scored true, :activity "slide", :time-expected 300 :unique-id 30}
                                 {:activity "writing-lesson", :time-expected 300 :unique-id 31}
                                 {:activity "writing-practice", :time-expected 300 :unique-id 32}
                                 {:scored true, :activity "magic-hat", :time-expected 300 :unique-id 33}
                                 {:scored true, :activity "cycling-letters", :time-expected 300 :unique-id 34}],
                        :lesson-sets
                                {:concepts-all    "concepts-all",
                                 :concepts-group  "l2-ls2-3",
                                 :concepts-single "l2-ls2-1"}}]}
             ]
  )
