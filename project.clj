(defproject pjagielski/disclojure "0.1.2"
  :description "A live coding environment for Overtone and Leipzig"
  :url "https://github.com/pjagielski/disclojure"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [overtone "0.10.0"]
                 [leipzig "0.10.0"]
                 [prismatic/plumbing "0.5.3"]]

  :deploy-repositories  [["releases" :clojars]])

