import numpy as np
import os
import PySpin
import matplotlib.pyplot as plt
import cv2
import math as maths
import math
import random as random
import scipy.ndimage
import scipy.signal
from scipy import optimize
from ximea import xiapi
from scipy import ndimage

class autofocus():
    
    def __init__(self):
        self.num_lines = 20
        self.z_stage_positions = []
        self.start_z_position = 8869.80
        self.range_z = 100.00
        self.end_z_position = self.start_z_position + self.range_z
        self.tiff_stack_list = []
        self.y0 = 524
        #self.y_bottom, self.y_top = 508, 508
        self.x0= 635#730#703
        #self.x_bottom, self.x_top = 715, 715
        self.kernel = np.ones((3,3),np.uint8)
        self.radius = 800
        self.ang_range_deg = 60
        self.showlines=0
        self.peakfind_fignum = 0#diagnostic fig number for peak finding - does stuff if >0
        self.diagnostics = self.peakfind_fignum+0
        self.cam = xiapi.Camera()
        self.camera_setup()
        for i in range(5):
            self.cent_img = xiapi.Image()
            self.centroid_image = self.cam.get_image(self.cent_img)
            self.cent_image_data = self.cent_img.get_image_data_numpy()
        self.radius_ignore, self.cent_image_data = self.calc_radius()
        #self.x0, self.y0 = self.calc_centroid(self.cent_image_data, self.x0, self.y0)
        self.y0, self.x0 = ndimage.measurements.center_of_mass(self.cent_image_data)
        self.y0, self.x0 = int(round(self.y0)), int(round(self.x0))
        print self.x0, self.y0

    def calc_centroid(self, data, x_centre, y_centre):
        pixel_sum = 0
        x_cent_sum = 0
        y_cent_sum = 0
        x_length = len(data)
        #print x_length
        y_length = np.size(data)/len(data)
        for x in range(0, x_length):
            for y in range(0, y_length):
                pixel_sum += data[x][y]
                x_cent_sum += (x-x_centre)*data[x][y]
                y_cent_sum += (y-y_centre)*data[x][y]
        #print pixel_sum, 'pixel'
        #print x_cent_sum, 'x'
        #print y_cent_sum, 'y'
        y_centroid = (x_cent_sum/pixel_sum)+x_centre
        x_centroid = (y_cent_sum/pixel_sum)+y_centre
        return x_centroid, y_centroid
        

    def give_radials(self, x_in,y_in,num_angles,radius,direction):
        if(direction==1):# R
            start_angle = 0-(self.ang_range_deg/2)
        elif(direction==2):#L
            start_angle = 180-(self.ang_range_deg/2)
        elif(direction==3):#DOWN
            start_angle = 90-(self.ang_range_deg/2)
        else:#DEFAULT TO 0 = UP
            start_angle = 270-(self.ang_range_deg/2)
        base_angle = maths.radians(start_angle)
        delta_angle = maths.radians(self.ang_range_deg/(num_angles-1))
        Angles = [(base_angle+(delta_angle*(x)))for x in range(0,num_angles)]
        x_array = [self.x0+radius*maths.cos(angle) for angle in Angles]
        y_array = [self.y0+radius*maths.sin(angle) for angle in Angles]        
        return x_array,y_array

    def find_peaks(self, line, fignum):
        np_line = np.array(line)
        #print np_line
        #print np.nonzero(np_line)[0]
        if np.nonzero(np_line)[0] != []:
          if np_line[0] == 0:
            for i in range(np.nonzero(np_line)[0][-1]):
                np_line[i] = 255
        peak_thresh = (0.2*(max(np_line)-min(np_line)))+min(np_line)
        #print peak_thresh
        low_bits = np_line < peak_thresh
        np_line[low_bits]=min(np_line)
        #print np_line
        #window is 21, order is 4 - see if this applies everywhere - maybe scale it to blob size?
        window = 21
        order = 4
        
        smooth = self.savitzky_golay(np_line, window, order)

        indices = [4]
        max_ = np.amax(smooth)
        if max_ == 255.0000000000022:
            indices.append(len(smooth)-1)
        else:    
         for i in range(0, len(smooth)):
            #print smooth[i]
            if smooth[i] == max_:
            #if smooth[i] == 271.29846355017787:
                indices.append(i)

        valatind = np.array([255. ,        max_])

        new_indices = []
        new_valatind = []
        ### Reconsider use of peak_threshold here - something higher maybe?
        for ctr in range(0,len(valatind-1)):
            if valatind[ctr]>peak_thresh:
                new_indices.append(indices[ctr])
                new_valatind.append(valatind[ctr])
        #print new_valatind
        #Easy fit into old framework
        vp = max(new_indices)

        if(fignum>0):
            prev_fig = plt.gcf()
            plt.figure(fignum)
            plt.plot(np_line)
            plt.figure(prev_fig.number)
        return vp

    def savitzky_golay(self, y, window_size, order, deriv=0, rate=1):
        from math import factorial
        
        try:
            window_size = np.abs(np.int(window_size))
            order = np.abs(np.int(order))
        except (ValueError, msg):
            raise ValueError("window_size and order have to be of type int")
        if window_size % 2 != 1 or window_size < 1:
            raise TypeError("window_size size must be a positive odd number")
        if window_size < order + 2:
            raise TypeError("window_size is too small for the polynomials order")
        order_range = range(order+1)
        half_window = (window_size -1) // 2
        # precompute coefficients
        b = np.mat([[k**i for i in order_range] for k in range(-half_window, half_window+1)])
        m = np.linalg.pinv(b).A[deriv] * rate**deriv * factorial(deriv)
        # pad the signal at the extremes with
        # values taken from the signal itself
        firstvals = y[0] - np.abs( y[1:half_window+1][::-1] - y[0] )
        lastvals = y[-1] + np.abs(y[-half_window-1:-1][::-1] - y[-1])
        y = np.concatenate((firstvals, y, lastvals))
        return np.convolve( m[::-1], y, mode='valid')

    def CC(self, Map, img_semicircle,thresh_min ,thresh_mode):
        shape = np.shape(Map)
        label_img, cc_num = scipy.ndimage.label(Map)
        '''
        plt.figure(1)
        plt.set_cmap('gray')
        plt.imshow(label_img)
        '''
        
        cc_areas = scipy.ndimage.sum(Map, label_img, range(cc_num+1))
        largest_area = max(cc_areas)/10
        #print largest_area*10
        #for cc_area in cc_areas:
            #if cc_area < largest_area/10:
                #cc_area = 0
        area_mask = (cc_areas < largest_area)
        label_img[area_mask[label_img]] = 0
        '''
        plt.figure(2)
        plt.imshow(label_img)
        plt.show()
        '''
        
        #print CC
        useful_CC = []
        #cc_areas = scipy.ndimage.sum(Map, label_img, range(cc_num+1))
        #largest_area = max(cc_areas)/10
        #print largest_area*10
        #area_mask = (cc_areas < largest_area)
        #label_img[area_mask[label_img]] = 0
        CC = scipy.ndimage.find_objects(label_img)
        #print CC
        for slices in CC:
          if slices != None:  
            #print slices
            if slices[0].start <= self.y0 and slices[0].stop >= self.y0 and slices[1].start <= self.x0 and slices[1].stop >= self.x0:
                useful_CC.append(slices)
            elif slices[0].start <= self.y0 and slices[0].stop >= self.y0 and slices[1].start <= self.x0 and slices[1].stop >= self.x0:
                useful_CC.append(slices)
         
        #print useful_CC
        if CC == [] or largest_area*10 < 1000000:# or useful_CC == []:
            new_semicircle = cv2.threshold(img_semicircle,thresh_mode,255,cv2.THRESH_BINARY)
            opening = new_semicircle[1]
            opening = cv2.morphologyEx(opening, cv2.MORPH_OPEN, self.kernel)
            inverse_binary = cv2.threshold(opening,60,255.0,cv2.THRESH_BINARY_INV)
            h, w = inverse_binary[1].shape[:2]
            mask = np.zeros((h+2, w+2), np.uint8)
            floodfill = cv2.floodFill(inverse_binary[1], mask, (0,0), 0)
            combined_image = opening + inverse_binary[1]
            shape = np.shape(combined_image)
            label_img, cc_num = scipy.ndimage.label(combined_image)
            CC = scipy.ndimage.find_objects(label_img)
            #print CC
            useful_CC = []
            for slices in CC:
                if slices[0].start <= self.y0 and slices[0].stop >= self.y0 and slices[1].start <= self.x0 and slices[1].stop >= self.x0:
                    useful_CC.append(slices)
                elif slices[0].start <= self.y0 and slices[0].stop >= self.y0 and slices[1].start <= self.x0 and slices[1].stop >= self.x0:
                    useful_CC.append(slices)
            cc_areas = scipy.ndimage.sum(combined_image, label_img, range(cc_num+1))
        elif useful_CC == [(slice(0L, 1024L, None), slice(0L, 1280L, None))]:
            new_semicircle = cv2.threshold(img_semicircle,thresh_min,255,cv2.THRESH_BINARY)
            opening = new_semicircle[1]
            opening = cv2.morphologyEx(opening, cv2.MORPH_OPEN, kernel)
            inverse_binary = cv2.threshold(opening,60,255.0,cv2.THRESH_BINARY_INV)
            h, w = inverse_binary[1].shape[:2]
            mask = np.zeros((h+2, w+2), np.uint8)
            floodfill = cv2.floodFill(inverse_binary[1], mask, (0,0), 0)
            combined_image = opening + inverse_binary[1]
            shape = np.shape(combined_image)
            label_img, cc_num = scipy.ndimage.label(combined_image)
            CC = scipy.ndimage.find_objects(label_img)
            #print CC
            useful_CC = []
            for slices in CC:
                if slices[0].start <= y_bottom and slices[0].stop >= y_bottom and slices[1].start <= x_bottom and slices[1].stop >= x_bottom:
                    useful_CC.append(slices)
                elif slices[0].start <= y_top and slices[0].stop >= y_top and slices[1].start <= x_top and slices[1].stop >= x_top:
                    useful_CC.append(slices)
            cc_areas = scipy.ndimage.sum(combined_image, label_img, range(cc_num+1))        
        else:
            cc_areas = scipy.ndimage.sum(Map, label_img, range(cc_num+1))
        #print cc_areas, max(cc_areas)

            
        #largest_area = max(cc_areas)
        #area_mask = (cc_areas < largest_area)
        #print area_mask
        #print useful_CC
        if np.size(useful_CC)> 2:
            CC_0_size = (useful_CC[0][0].stop-useful_CC[0][0].start)*(useful_CC[0][1].stop-useful_CC[0][1].start)
            CC_1_size = (useful_CC[1][0].stop-useful_CC[1][0].start)*(useful_CC[1][1].stop-useful_CC[1][1].start)
            if CC_0_size >= CC_1_size:
                useful_CC = [useful_CC[0]]
            else:
                useful_CC = [useful_CC[1]]
        #print useful_CC
        area_mask2 = [True]

        #plt.figure(3)
        #plt.imshow(label_img)
        #plt.show()
        if useful_CC != []:
         for ccs in CC:
            #print ccs, useful_CC[0]
            area_mask1 = (useful_CC[0] != ccs)
            area_mask2.append(area_mask1)

         area_mask2 = np.asarray(area_mask2)
        #print area_mask2
        #area_mask = list(set(CC) - set(temp2)) 
         label_img[area_mask2[label_img]] = 0

        else:
            Map = label_img
            label_img, cc_num = scipy.ndimage.label(Map)
            cc_areas = scipy.ndimage.sum(Map, label_img, range(cc_num+1))
            largest_area = max(cc_areas)
            area_mask = (cc_areas < largest_area)
            label_img[area_mask[label_img]] = 0



        return label_img, CC

    def camera_setup(self):

        self.cam.open_device()
        
        self.cam.set_ae_max_limit(100000)
        self.cam.set_ag_max_limit(0)
        self.cam.set_exp_priority(0.8)
        self.cam.set_aeag_level(10)
        self.cam.set_aeag_roi_width(85)
        self.cam.set_aeag_roi_height(85)
        self.cam.set_aeag_roi_offset_x(660)
        self.cam.set_aeag_roi_offset_y(478)
        self.cam.enable_aeag()
        
        #self.cam.set_param('exposure',5000)
        self.cam.set_imgdataformat('XI_MONO8')
        self.cam.start_acquisition()

        print('Starting data acquisition...')

    def camera_close(self):
        self.cam.stop_acquisition()
        print('Stopping acquisition...')
        self.cam.close_device()
        print('Closing first camera...')

    def calc_radius(self):
        image_width = 0
        image_height = 0
        img = xiapi.Image()
        self.cam.get_image(img)
        image_data = img.get_image_data_numpy()#
        ellipse_x = []
        ellipse_y = []
        
        if image_width == 0:
            image_width = np.size(image_data)/len(image_data)
            image_height = len(image_data)
        semicircle = image_data
        image = image_data
        cv2.normalize(semicircle,semicircle,0,255,cv2.NORM_MINMAX)
        #print(np.amax(data))
        '''
        plt.figure(235)
        plt.set_cmap('gray')
        plt.scatter(x0,y0)
        plt.imshow(semicircle)
        '''

        img_semicircle = semicircle.astype(np.uint8)
        thresh_x_range = range(image_width-50,image_width,1)
        thresh_x_range1 = range(0,100,1)
        left_range = range(self.x0-200, self.x0-100,1)
        right_range = range(self.x0+101, self.x0+201, 1)
        up_range = range(self.y0-100,self.y0+101,1)
        new_x_range = range(self.x0-50,self.x0+50,1)
        #thresh_min = np.amax(image[:,thresh_x_range1][:100])
        #thresh_min = 30
        #print thresh_min
        thresh_min = np.sort(image[:,thresh_x_range][:100].ravel())[int(round(0.99*len(image[:,thresh_x_range][:100].ravel())))]
        #thresh_min = sort(image[:,thresh_x_range][:50].ravel())[int(round(0.70*len(image[:,thresh_x_range][:50].ravel())))]
        #print thresh_min
        #thresh_min = np.amax(image[:,thresh_x_range][900:])
        #thresh_mean = np.mean(image[:,thresh_x_range][:200])
        #thresh_median = np.median(image[:,thresh_x_range][900:])
        #thresh_mode1 = scipy.stats.mode(image[:,thresh_x_range1][:200], axis=None)[0][0] +1 
        thresh_mode = scipy.stats.mode(image[:,thresh_x_range][:200], axis=None)[0][0] +1
        #thresh_mode2 = scipy.stats.mode(image[:,new_x_range][1:101], axis=None)[0][0]
        #thresh_mode3 = scipy.stats.mode(image[:,new_x_range][750:850], axis=None)[0][0]
        #thresh_mode_up = np.mean(image[:,new_x_range][1:101])
        #thresh_mode_down = np.mean(image[:,new_x_range][750:850])
        #thresh_mean_left = np.amax(image[:,left_range][up_range])
        #thresh_mean_right = np.amax(image[:,right_range][up_range])
        thresh_mean_left = np.sort(image[:,left_range][up_range].ravel())[int(round(0.8*len(image[:,left_range][up_range].ravel())))] 
        thresh_mean_right = np.sort(image[:,right_range][up_range].ravel())[int(round(0.8*len(image[:,right_range][up_range].ravel())))] 
        if thresh_mean_left <= thresh_mean_right:
            used_thresh = thresh_mean_left
        else:
            used_thresh = thresh_mean_right
        '''    
        if thresh_mode_up <= thresh_mode_down:
            used_thresh_mode = thresh_mode
        else:
            used_thresh_mode = thresh_min
        '''
        #print used_thresh_mode
        #print thresh_min, thresh_mode, thresh_mode1, thresh_median, thresh_mean
        #print thresh_mode2, thresh_mode3
        new_semicircle = cv2.threshold(img_semicircle,used_thresh,255,cv2.THRESH_BINARY)
        #opening = semicircle
        opening = new_semicircle[1]
        opening = cv2.morphologyEx(opening, cv2.MORPH_OPEN, self.kernel)
        opening[0] = 0
        opening[:, 0] = 0
        inverse_binary = cv2.threshold(opening,60,255.0,cv2.THRESH_BINARY_INV)
        h, w = inverse_binary[1].shape[:2]
        mask = np.zeros((h+2, w+2), np.uint8)
        
        floodfill = cv2.floodFill(inverse_binary[1], mask, (0,0), 0)
        #print floodfill
        #combined_image = opening + floodfill[1]
        combined_image = opening + inverse_binary[1]
        
        '''
        plt.figure(79)
        plt.set_cmap('gray')
        plt.imshow(combined_image)
        plt.scatter(x0,y0)
        plt.show()
        '''
        
        combined_image2, label = self.CC(combined_image, img_semicircle, thresh_min, thresh_mode)
        '''
        plt.figure(76)
        plt.set_cmap('gray')
        plt.imshow(combined_image2)
        plt.scatter(x0,y0)
        plt.show()
        '''
        combined_image3 = np.uint8(combined_image2)
        scaled_down_image = combined_image3
        major = cv2.__version__.split('.')[0]
        if major == '3':
            gsxgxg, contours,hierarchy = cv2.findContours(scaled_down_image, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)
        
        else:
            contours,hierarchy = cv2.findContours(scaled_down_image, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)
        cnt = contours
        if cnt != []:
            hull = cv2.convexHull(np.array(cnt[-1]))
            mask2 = np.zeros(combined_image.shape[:2], np.uint8)
            contour_image = cv2.drawContours(mask2, [hull], -1, 255, -1 )
            contour_image = mask2
        else:
            combined_image2[combined_image2>255] = 255
            contour_image = combined_image2
        data = contour_image
        y_centre, x_centre = ndimage.measurements.center_of_mass(data)

        '''
        needed for radii

        
        plt.figure(76)
        plt.set_cmap('gray')
        plt.imshow(data)
        plt.scatter(x0,y0)
        plt.show()
        
        right_range = range(self.x0,np.size(data)/len(data),1)
        left_range = range(0,self.x0,1)
        right_sum = np.sum(data[:,right_range])
        left_sum = np.sum(data[:,left_range])
        
        if right_sum == 0 and left_sum == 0:
            right_sum = np.sum(data[:,right_range])
            left_sum = np.sum(data[:,left_range])


        if left_sum > right_sum:
            x_array,y_array = self.give_radials(self.x0,self.y0,self.num_lines,self.radius,2)
        else:
            x_array,y_array = self.give_radials(self.x0,self.y0,self.num_lines,self.radius,1)
        
        #if thresh_mean_left > thresh_mean_right:#
        if left_sum > right_sum:
            x_array,y_array = self.give_radials(self.x0,self.y0,self.num_lines,self.radius,2)
        else:
            x_array,y_array = self.give_radials(self.x0,self.y0,self.num_lines,self.radius,1)
            
        for idx in range (0,len(y_array)):

            num = pow((pow((x_array[idx]-self.x0),2)+pow((y_array[idx]-self.y0),2)),0.5)
            #Define start, endpoint arrays
            xQ_init,yQ_init =  np.linspace(self.x0, x_array[idx], int(num)), np.linspace(self.y0, y_array[idx], int(num))

            xQ, yQ = [], []
            for index_ in range(0,len(xQ_init)):
                if np.int(xQ_init[index_])<1280 and np.int(yQ_init[index_])< 1024 and np.int(xQ_init[index_])>0 and np.int(yQ_init[index_])>0:
                    xQ.append(xQ_init[index_])
                    yQ.append(yQ_init[index_])
            xQ = np.asarray(xQ)
            yQ = np.asarray(yQ)
            #xQ,yQ =  np.linspace(0, 800, 1024), np.linspace(600+(10*idx), 700+(10*idx), int(1024))
            #Measure along line
            zi = data[yQ.astype(np.int), xQ.astype(np.int)]
            #zi_2 = semicircle[yQ.astype(np.int), xQ.astype(np.int)]
            #print zi
            #zi = scipy.ndimage.interpolation.map_coordinates(data, np.vstack((yQ,xQ)))
            #if (showlines>0):
                #plt.plot(zi)
            maxloc = self.find_peaks(zi,self.peakfind_fignum)
            
            #shit_maxloc = find_peaks(zi_2,peakfind_fignum)
            #plt.figure(3)
            #plt.scatter(x0,y0, color = 'r')
            #plt.scatter(xQ[maxloc],yQ[maxloc], color = 'b')
            ellipse_x.append(xQ[maxloc])
            ellipse_y.append(yQ[maxloc])


        #### Filter values that seem off - RELIES ON SENSIBLE CENTRE! ####
        rad_dist = [val*0 for val in ellipse_x]
        for k in range (0, len(ellipse_x)-1):
            rad_dist[k] = math.sqrt(math.pow((ellipse_x[k]-self.x0),2)+math.pow((ellipse_y[k]-self.y0),2))
        
        r_d = np.array(rad_dist)
        
        for i in range(len(r_d)):
            if r_d[i] > 0 and r_d[i] < 20:
                r_d[i] = 800
            elif r_d[i] < 0 and r_d[i] <20:
                r_d[i] = -800
        
        #print r_d
        std = np.std(r_d)
        mean = np.mean(r_d)
        #print(r_d)
        #print(std)
        std_adj = std*0.25

        good_Rs = [val for val in r_d if val>(mean-std) and val <(mean+std)]
    
        if left_sum > right_sum:
            calc_radius = np.mean(good_Rs)  
     
        else:
            calc_radius = -1*np.mean(good_Rs)
        
        return calc_radius, data
        '''
        return x_centre, data
        

    def main(self):
        #self.camera_setup()
        radius_send, image_not_send = self.calc_radius()
        return radius_send
    
        
        
        




        








