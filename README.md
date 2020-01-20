# SHealth-Demo
Samsung health android demo application for tracking daily step count.


Require
1. samsung-health-data-v1.4.0.jar need to keep inside app->lib folder
2. In Android-Manifest file add below line
  
  
  <meta-data
  
            android:name="com.samsung.android.health.permission.read"
            
            android:value="com.samsung.health.step_count;/>
            
  The above line is manadatory.
