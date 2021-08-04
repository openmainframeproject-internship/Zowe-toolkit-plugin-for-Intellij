Useful links:
   
  - documentation: https://docs.github.com/en/actions

  - where I put my workflows: https://github.com/MaliMi97/For-Mainframe/tree/GHactions/.github/workflows   (note: the version 0.2 of the plugin is still veing sued in this branch)

Simple workflow:


  - This will be a summary of what I read from the documentation and tried to implement. It should be noted that because of testing reasons, some workflows might have a workflow_dispatch option, which allows the user to trigger the action manually whenever they want. This option works only if the file is in default branch.

  - The simplest workflow, which can be used from our purposes can be pretty much copied from the documentation https://docs.github.com/en/actions/guides/building-and-testing-java-with-gradle. My equivalent is here https://github.com/MaliMi97/For-Mainframe/blob/GHactions/.github/workflows/oneJob.yml. There are two thing to note:

    - The command **./gradlew build** automatically runs unit tests after build.
      - if you want to run unit tests separately, you should use **./gradlew build -x test**  the **-x test** means that the test task will be excluded.
  
    - According to the documentation https://docs.github.com/en/actions/guides/building-and-testing-java-with-gradle, some cache files need to be removed before the cache upload actually happens in order for teh caching to run smoothly. 


Trying to run test in parallel:

  - The idea is that as more and more api and especially ui tests will be implemented, it might be favourable to run the tests in parallel. The GitHub actions automatically, unless said otherwise, runs the jobs in workflow in parallel, which means that all we need to do is to make independ jobs. But this means that there will be a necessity to build gradle in each job, which will slow down the workflow. So for a small amount of tests, running the test in parallel might not only be unnecessary, but will also be detrimental. Another complication migth arise with caching. We do not know for sure, when will a new runner be allocated. So there might be an issue when if one job downloads cache and other job uploads cache with the same key at the same time. Here are some ideas, which I have been toying with and tried to implement:
